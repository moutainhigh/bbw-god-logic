package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.rechargeactivities.wartoken.event.WarTokenEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：战令
 *
 * @author lwb
 * date 2021-06-02
 */
@Service
public class WarTokenLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;

    /**
     * 是否开启战令
     *
     * @param uid
     * @return
     */
    public boolean openWarToken(long uid) {
        IActivity activity = activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.WAR_TOKEN);
        if (activity == null) {
            return false;
        }
        if (gameUserService.getGameUser(uid).getLevel() < 18) {
            return false;
        }
        return true;
    }

    /**
     * 获取/创建
     * 非战令期间获取将抛出提示
     *
     * @param uid
     * @return
     * @throws ExceptionForClientTip
     */
    public UserWarToken getOrCreateUserWarToken(Long uid) {
        if (!openWarToken(uid)) {
            throw new ExceptionForClientTip("wartoken.not.open");
        }
        IActivity activity = activityService.getGameActivity(gameUserService.getActiveSid(uid), ActivityEnum.WAR_TOKEN);
        UserWarToken warToken = gameUserService.getSingleItem(uid, UserWarToken.class);
        synchronized (uid) {
            if (warToken == null) {
                warToken = UserWarToken.getInstance(uid, activity.gainId());
                gameUserService.addItem(uid, warToken);
                initTaskList(warToken, true);
                return warToken;
            }
            if (!warToken.getActivityId().equals(activity.gainId())) {
                warToken.init(activity.gainId());
                initTaskList(warToken, true);
                gameUserService.updateItem(warToken);
            } else if (DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(new Date())) > warToken.getInitDate()) {
                warToken.initWeekData();
                initTaskList(warToken, false);
                gameUserService.updateItem(warToken);
            }
        }
        return warToken;
    }

    /**
     * 获取玩家任务列表
     *
     * @param userWarToken
     * @return
     */
    public List<UserWarTokenTask> getUserTasks(UserWarToken userWarToken) {
        List<UserWarTokenTask> tasks = gameUserService.getMultiItems(userWarToken.getGameUserId(), UserWarTokenTask.class);
        List<UserWarTokenTask> collect = tasks.stream().filter(p -> p.ifValid(userWarToken.getActivityId())).collect(Collectors.toList());
        if (tasks.size() != collect.size()) {
            //删除过期的数据
            List<UserWarTokenTask> expiredList = tasks.stream().filter(p -> !p.ifValid(userWarToken.getActivityId())).collect(Collectors.toList());
            gameUserService.deleteItems(userWarToken.getGameUserId(), expiredList);
        }
        return collect;
    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param add
     */
    public void addTaskProgress(long uid, int taskId, int add) {
        if (!openWarToken(uid)) {
            return;
        }
        getOrCreateUserWarToken(uid);
        CfgWarTokenTask cfgTokenTask = WarTokenTool.getCfgWarTokenTask(taskId);
        if (cfgTokenTask == null) {
            return;
        }
        UserWarTokenTask task = gameUserService.getCfgItem(uid, taskId, UserWarTokenTask.class);
        if (task == null || (task.getGainTimes() > 0 && !task.isPollTask())) {
            return;
        }
        task.addVal(add);
        int times = task.gainAwards(cfgTokenTask);
        gameUserService.updateItem(task);
        WarTokenEventPublisher.pubAddExpEvent(uid, times * cfgTokenTask.getExp(), cfgTokenTask.isAddWeekExp());
    }

    /**
     * 初始化任务列表
     *
     * @param warToken
     */
    public void initTaskList(UserWarToken warToken, boolean firstInit) {
        long uid = warToken.getGameUserId();
        long activityId = warToken.getActivityId();
        List<UserWarTokenTask> warTokenTasks = new ArrayList<>();
        List<CfgWarTokenTask> buildTasks = new ArrayList<>();
        //所有常规任务
        buildTasks.addAll(WarTokenTool.getCfgWarTokenTasks(WarTokenTaskType.NORMAL_TASK));
        //随机3个每周的随机任务
        List<CfgWarTokenTask> tasks = WarTokenTool.getCfgWarTokenTasks(WarTokenTaskType.RANDOM_TASK);
        buildTasks.addAll(PowerRandom.getRandomsFromList(3, tasks));
        //添加登录任务
        int loginTaskId = WarTokenTool.LOGIN_TASK_IDS.get(0);
        if (warToken.getSupToken() > 0) {
            loginTaskId = WarTokenTool.LOGIN_TASK_IDS.get(2);
        }
        UserWarTokenTask loginTask = UserWarTokenTask.getInstance(uid, WarTokenTool.getCfgWarTokenTask(loginTaskId), activityId);
        loginTask.setPollTask(false);
        loginTask.setTotal(1);
        warTokenTasks.add(loginTask);
        //添加每周唯一任务
        if (firstInit) {
            //首次初始化  固定为 封神台胜利20次任务
            buildTasks.add(WarTokenTool.getCfgWarTokenTask(400050));
        } else {
            List<CfgWarTokenTask> uniqueTasks = WarTokenTool.getCfgWarTokenTasks(WarTokenTaskType.RANDOM_UNIQUE_TASK);
            buildTasks.add(PowerRandom.getRandomFromList(uniqueTasks));
        }
        for (CfgWarTokenTask cfgWarTokenTask : buildTasks) {
            warTokenTasks.add(UserWarTokenTask.getInstance(uid, cfgWarTokenTask, activityId));
        }
        gameUserService.addItems(warTokenTasks);
    }

    /**
     * 加经验
     *
     * @param uid
     * @param val
     */
    public void addWarTokenExp(long uid, int val, boolean addWeekExp) {
        UserWarToken warToken = getOrCreateUserWarToken(uid);
        if (addWeekExp) {
            int limit = WarTokenTool.getWeekMaxExp(gameUserService.getActiveSid(uid));
            int maxWeekExp = limit - warToken.getWeekTaskExp();
            if (maxWeekExp > 0) {
                val = Math.min(maxWeekExp, val);
            } else {
                return;
            }
            warToken.setWeekTaskExp(warToken.getWeekTaskExp() + val);
        }
        warToken.addExp(val);
        gameUserService.updateItem(warToken);
    }

    /**
     * 是否可以购买令牌
     *
     * @param uid
     * @return
     */
    public boolean checkCanBuyWarToken(long uid) {
        if (!openWarToken(uid)) {
            return false;
        }
        UserWarToken warToken = getOrCreateUserWarToken(uid);
        return warToken.getSupToken() == 0;
    }

    /**
     * 下发战令
     *
     * @param userReceipt
     * @param pid
     * @param uid
     */
    public void dispatch(UserReceipt userReceipt, int pid, long uid) {
        if (!openWarToken(uid)) {
            userReceipt.setResult("战令尚未开启，标记失败！");
            return;
        }
        UserWarToken warToken = getOrCreateUserWarToken(uid);
        if (warToken.getSupToken() > 0) {
            userReceipt.setResult("战令重复购买！");
            return;
        }
        int supToken = CfgProductGroup.CfgProduct.WAR_TOKEN_SUP == pid ? 2 : 1;
        warToken.setSupToken(supToken);
        gameUserService.updateItem(warToken);
        WarTokenEventPublisher.pubActiveEvent(uid);
    }
}
