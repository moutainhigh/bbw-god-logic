package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.common.ListUtil;
import com.bbw.god.city.mixd.event.EPPassTier;
import com.bbw.god.city.mixd.event.PassTierEvent;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.cunz.event.CunZTaskEventPublisher;
import com.bbw.god.gameuser.task.timelimit.event.EPTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskAchievedEvent;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import com.bbw.god.server.fst.event.GameFstFightOverEvent;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouPassEvent;
import com.bbw.god.server.maou.alonemaou.event.EPPassAloneMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CunZTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCunZTaskService userCunZTaskService;

    /**
     * 101005
     * 第一神豪-史诗级-绝境反击（二）
     * 资金不足，请从交易中获利0/100万作为资金
     *
     * @param event
     */
    @Async
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        if (ep.getWay() != WayEnum.TRADE) {
            return;
        }
        long uid = ep.getGuId();
        achieveTask(uid, 101005, ep.getWeekCopper());
    }

    /**
     * 102005
     * 神神叨叨的老者-史诗级-青胜于蓝（二）
     * 替老者查看文王六十四卦
     *
     * @param event
     */
    @Async
    @EventListener
    public void deductGold(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        if (ep.getWay() != WayEnum.HEXAGRAM) {
            return;
        }
        long uid = ep.getGuId();
        achieveTask(uid, 102005, 1);
    }

    /**
     * 103005
     * 村里的二毛-史诗级-天选之子（二）
     * 参与0/15场跨服封神台，跟上二毛的脚步
     *
     * @param event
     */
    @Async
    @EventListener
    public void uplevelCard(GameFstFightOverEvent event) {
        BaseEventParam ep = event.getEP();
        long uid = ep.getGuId();
        achieveTask(uid, 103005, 1);
    }

    /**
     * 104005
     * 村里的小张-史诗级-九死一生（二）
     * 通过0/5层梦魇迷仙洞，离开这里
     *
     * @param event
     */
    @Async
    @EventListener
    public void passMxd(PassTierEvent event) {
        EPPassTier ep = event.getEP();
        long uid = ep.getGuId();
        achieveTask(uid, 104005, 1);
    }


    /**
     * 105005
     * 村长大叔-史诗级-灭顶之灾（二）
     * 战胜0/6层独战魔王，抵挡妖魔们的攻击
     *
     * @param event
     */
    @Async
    @EventListener
    public void passAloneMaou(AloneMaouPassEvent event) {
        EPPassAloneMaou ep = event.getEP();
        long uid = ep.getGuId();
        achieveTask(uid, 105005, 1);
    }


    /**
     * 106005 村里的小布-史诗级-初次登场（二） 获得0/15场神仙大会胜利，跟上小布的脚步  每日
     * 107005 村里的小巴-史诗级-以子为傲（二）路遭拦阻，战胜0/10个精英野怪   没有精英野怪，每日有普通野怪
     *
     * @param event
     */
    @Async
    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = event.getEP();
        long uid = ep.getGuId();
        if (ep.getFightType() == FightTypeEnum.SXDH) {
            achieveTask(uid, 106005, 1);
        } else if (ep.getFightType() == FightTypeEnum.YG) {
            if (ep.getFightSubmit().getYeGuaiType() == YeGuaiEnum.YG_ELITE) {
                achieveTask(uid, 107005, 1);
            }
        } else if (ep.getFightType() == FightTypeEnum.CZ_TASK_FIGHT) {
            Long fightTaskId = ep.getFightSubmit().getFightTaskId();
            Optional<UserTimeLimitTask> userData = gameUserService.getUserData(uid, fightTaskId, UserTimeLimitTask.class);
            if (!userData.isPresent()) {
                return;
            }
            achieveTask(userData.get(), 1);
        }

    }

    /**
     * 村庄任务统计
     *
     * @param event
     */
    @Order
    @EventListener
    public void finishCunZTask(TimeLimitTaskAchievedEvent event) {
        EPTimeLimitTask ep = event.getEP();
        Long uid = ep.getGuId();
        if (ep.getTaskGroup() != TaskGroupEnum.CUN_ZHUANG_TASK) {
            return;
        }
        boolean isFirstAchieved = true;
        UserCunzTasksInfo cunzTasksInfo = gameUserService.getSingleItem(uid, UserCunzTasksInfo.class);
        if (null == cunzTasksInfo) {
            cunzTasksInfo = UserCunzTasksInfo.instance(uid);
            gameUserService.addItem(uid, cunzTasksInfo);
        } else {
            isFirstAchieved = !cunzTasksInfo.getAchievedIds().contains(ep.getTaskId());
        }
        cunzTasksInfo.addAchieved(ep.getTaskId());
        cunzTasksInfo.addDifficultyFinishedNum(ep.getTaskId());
        gameUserService.updateItem(cunzTasksInfo);
        CunZTaskEventPublisher.pubCunZTaskAchievedEvent(uid, ep.getTaskId(), isFirstAchieved);
    }

    private void achieveTask(long uid, int taskId, long addedNum) {
        List<UserTimeLimitTask> uts = userCunZTaskService.getTasks(uid);
        uts = uts.stream()
                .filter(tmp -> tmp.getBaseId() == taskId && tmp.getStatus() == TaskStatusEnum.DOING.getValue())
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTimeLimitTask ut : uts) {
            ut.addValue(addedNum);
            if (ut.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
                TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.CUN_ZHUANG_TASK, taskId);
            }
        }
        gameUserService.updateItems(uts);
    }

    private void achieveTask(UserTimeLimitTask ut, int addedNum) {
        ut.addValue(addedNum);
        if (ut.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(ut.getGameUserId(), TaskGroupEnum.fromValue(ut.getGroup()), ut.getBaseId());
        }
        gameUserService.updateItem(ut);
    }
}
