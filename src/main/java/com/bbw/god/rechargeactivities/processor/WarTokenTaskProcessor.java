package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.item.RDAchievableItem;
import com.bbw.god.rechargeactivities.RDWarToken;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.wartoken.*;
import com.bbw.god.rechargeactivities.wartoken.event.WarTokenEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明： 战令-任务
 *
 * @author lwb
 * date 2021-06-02
 */
@Service
public class WarTokenTaskProcessor extends AbstractWarTokenProcessor {
    /** 周战令任务 */
    private static final List<Integer> WEEK_TASK = Arrays.asList(100000, 100001, 100002);

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.WAR_TOKEN_TASK;
    }

    @Override
    public RDWarToken listAwards(long uid) {
        UserWarToken warToken = getUserWarToken(uid);
        List<UserWarTokenTask> tasks = warTokenLogic.getUserTasks(warToken);
        List<RDAchievableItem> rdTasks = new ArrayList<>();
        for (UserWarTokenTask userTask : tasks) {
            RDAchievableItem item = buildTask(userTask, warToken.getSupToken() > 0);
            if (item == null) {
                continue;
            }
            rdTasks.add(item);
        }
        RDWarToken rdWarToken = RDWarToken.getInstance(warToken);
        rdWarToken.setRefreshTimes(warToken.getRefreshTimes());
        rdWarToken.setWeekTaskExp(warToken.getWeekTaskExp());
        int weekMaxExp = WarTokenTool.getWeekMaxExp(gameUserService.getActiveSid(uid));
        rdWarToken.setWeekTaskMaxExp(weekMaxExp);
        // 当玩家每周的任务经验达到上限，将隐藏除登陆任务以外的其他任务
        if (warToken.getWeekTaskExp() >= weekMaxExp) {
            rdTasks = rdTasks.stream().filter(tmp -> WEEK_TASK.contains(tmp.getId())).collect(Collectors.toList());
        }
        rdTasks.sort(Comparator.comparing(RDAchievableItem::getId));
        rdWarToken.setTasks(rdTasks);
        return rdWarToken;
    }

    private RDAchievableItem buildTask(UserWarTokenTask userTask, boolean supToken) {
        RDAchievableItem rdTask = new RDAchievableItem();
        rdTask.setId(userTask.getBaseId());
        CfgWarTokenTask tokenTask = WarTokenTool.getCfgWarTokenTask(userTask.getBaseId());
        if (tokenTask == null) {
            //任务已删除
            return null;
        }
        rdTask.setProgress(userTask.getTotal() % tokenTask.getNeed());
        rdTask.setTotalProgress(tokenTask.getNeed());
        rdTask.setStatus(AwardStatus.UNAWARD.getValue());
        rdTask.setMemo(String.format("完成%s次", userTask.getGainTimes()));
        if (WarTokenTool.LOGIN_TASK_IDS.contains(userTask.getBaseId())) {
            rdTask.setProgress(userTask.getTotal());
            AwardStatus status = userTask.getGainTimes() == 0 ? AwardStatus.ENABLE_AWARD : AwardStatus.AWARDED;
            if (AwardStatus.ENABLE_AWARD.equals(status) && WarTokenTool.LOGIN_TASK_IDS.get(1).equals(userTask.getBaseId()) && !supToken) {
                //未开通进阶 则为继续领取
                status = AwardStatus.CONTINUE_AWARD;
            }
            rdTask.setStatus(status.getValue());
            rdTask.setMemo("不计入本周任务经验");
        }
        if (!userTask.isPollTask() && userTask.getGainTimes() > 0) {
            rdTask.setStatus(AwardStatus.AWARDED.getValue());
        }
        return rdTask;
    }

    @Override
    public RDWarToken gainAwards(long uid, int realId) {
        UserWarToken warToken = getUserWarToken(uid);
        List<UserWarTokenTask> tasks = warTokenLogic.getUserTasks(warToken);
        Optional<UserWarTokenTask> optional = tasks.stream().filter(p -> WarTokenTool.LOGIN_TASK_IDS.contains(p.getBaseId())).findFirst();
        if (!optional.isPresent()) {
            return new RDWarToken();
        }
        UserWarTokenTask task = optional.get();
        if (task.getGainTimes() > 0) {
            return new RDWarToken();
        }
        CfgWarTokenTask cfgWarTokenTask = WarTokenTool.getCfgWarTokenTask(task.getBaseId());
        if (WarTokenTool.LOGIN_TASK_IDS.get(0).equals(task.getBaseId())) {
            //如果当前为1000 经验的 登录任务  替换成2000的
            task.setBaseId(WarTokenTool.LOGIN_TASK_IDS.get(1));
        } else if (warToken.getSupToken() == 0) {
            throw new ExceptionForClientTip("wartoken.need.sup");
        } else {
            task.setGainTimes(1);
        }
        gameUserService.updateItem(task);
        WarTokenEventPublisher.pubAddExpEvent(uid, cfgWarTokenTask.getExp(), cfgWarTokenTask.isAddWeekExp());
        RDWarToken rd = new RDWarToken();
        rd.addTreasure(new RDCommon.RDTreasureInfo(TreasureEnum.WAR_TOKEN_EXP.getValue(), cfgWarTokenTask.getExp()));
        return rd;
    }

    @Override
    public RDWarToken refreshItem(long uid, int id) {
        List<CfgWarTokenTask> cfgWarTokenTasks = WarTokenTool.getCfgWarTokenTasks(WarTokenTaskType.RANDOM_TASK);
        List<Integer> ids = cfgWarTokenTasks.stream().map(CfgWarTokenTask::getId).collect(Collectors.toList());
        if (!ids.contains(id)) {
            throw new ExceptionForClientTip("wartoken.cant.refresh.task.id");
        }
        UserWarToken warToken = getUserWarToken(uid);
        List<UserWarTokenTask> userTasks = warTokenLogic.getUserTasks(warToken);
        Optional<UserWarTokenTask> optional = userTasks.stream().filter(p -> p.getBaseId().equals(id)).findFirst();
        if (!optional.isPresent()) {
            return new RDWarToken();
        }
        //首次刷新免费，后续每次50元宝，不会刷新到已有的任务。
        RDWarToken rd = new RDWarToken();
        int needGold = 50;
        if (warToken.getRefreshTimes() == 0) {
            needGold = 0;
        }
        if (needGold > 0) {
            ResChecker.checkGold(gameUserService.getGameUser(uid), needGold);
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.WAR_TOKEN_REFRESH_TASK, rd);
        }
        List<Integer> buildTaskIds = userTasks.stream().filter(p -> ids.contains(p.getBaseId())).map(UserWarTokenTask::getBaseId).collect(Collectors.toList());
        List<CfgWarTokenTask> collect = cfgWarTokenTasks.stream().filter(p -> !buildTaskIds.contains(p.getId())).collect(Collectors.toList());
        gameUserService.deleteItem(optional.get());
        UserWarTokenTask task = UserWarTokenTask.getInstance(uid, PowerRandom.getRandomFromList(collect), warToken.getActivityId());
        warToken.setRefreshTimes(warToken.getRefreshTimes() + 1);
        gameUserService.updateItem(warToken);
        gameUserService.addItem(uid, task);
        rd.setTask(buildTask(task, warToken.getSupToken() > 0));
        return rd;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        UserWarToken warToken = getUserWarToken(uid);
        List<UserWarTokenTask> tasks = warTokenLogic.getUserTasks(warToken);
        Optional<UserWarTokenTask> optional = tasks.stream().filter(p -> WarTokenTool.LOGIN_TASK_IDS.contains(p.getBaseId())).findFirst();
        if (!optional.isPresent()) {
            return 0;
        }
        UserWarTokenTask task = optional.get();
        if (task.getGainTimes() > 0 || (warToken.getSupToken() == 0 && WarTokenTool.LOGIN_TASK_IDS.indexOf(task.getBaseId()) > 0)) {
            return 0;
        }
        return 1;
    }
}
