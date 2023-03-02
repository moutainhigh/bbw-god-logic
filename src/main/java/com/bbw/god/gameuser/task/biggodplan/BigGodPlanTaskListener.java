package com.bbw.god.gameuser.task.biggodplan;

import com.bbw.god.activity.processor.BigGodPlanProcessor;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.EPCardLevelUp;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.card.event.UserCardLevelUpEvent;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.special.event.EPSpecialAdd;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialAddEvent;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.biggodplan.event.CombinedServiceDiscountEvent;
import com.bbw.god.gameuser.task.biggodplan.event.EPCombinedServiceDiscount;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.task.timelimit.cunz.event.CunZTaskAchievedEvent;
import com.bbw.god.gameuser.task.timelimit.cunz.event.EPCunZTask;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.mall.snatchtreasure.event.EPSnatchTreasureDraw;
import com.bbw.god.mall.snatchtreasure.event.SnatchTreasureDrawEvent;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouKilledEvent;
import com.bbw.god.server.maou.alonemaou.event.EPAloneMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 大仙计划任务监听器
 *
 * @author: huanghb
 * @date: 2022/2/15 16:39
 */
@Slf4j
@Component
@Async
public class BigGodPlanTaskListener {
    @Autowired
    private BigGodPlanTaskDataService bigGodPlanTaskDataService;
    @Autowired
    private BigGodPlanTaskService bigGodPlanTaskService;
    @Autowired
    private BigGodPlanProcessor bigGodPlanProcessor;
    @Autowired
    private GameUserService gameUserService;

    private static final List<Integer> LOGIN_TASK_ID = Arrays.asList(180101, 180201, 180301, 180401, 180501, 180601, 180701);
    private static final List<Integer> Dice_TASK_ID = Arrays.asList(180102, 180202, 180302, 180402, 180502, 180602, 180702);
    /** 最大天数 */
    private static final int MAX_DAYS = 8;
    private static final int TASK_PROGRESS_ADD_BASE_VALUE = 1;


    /**
     * 登录事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void login(LoginEvent event) {
        LoginPlayer player = event.getLoginPlayer();
        Long uid = player.getUid();
        addProgress(uid, LOGIN_TASK_ID, TASK_PROGRESS_ADD_BASE_VALUE);
    }

    /**
     * 体力扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void deductDice(DiceDeductEvent event) {
        EPDiceDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        int value = ep.getDeductDice();
        addProgress(uid, Dice_TASK_ID, value);
    }

    /**
     * 合服折扣抽奖事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void combinedServiceDiscount(CombinedServiceDiscountEvent event) {
        EPCombinedServiceDiscount ep = event.getEP();
        Long uid = ep.getGuId();
        addProgress(uid, 180103, TASK_PROGRESS_ADD_BASE_VALUE);
    }

    /**
     * 特产扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void deductSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        int value = ep.getSpecialInfoList().size();
        switch (way) {
            case TRADE:
                addProgress(uid, 180204, value);
                break;
            default:
                break;
        }
    }

    /**
     * 特产添加事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addSpecial(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        int value = ep.getAddSpecials().size();
        switch (way) {
            case YSG:
                addProgress(uid, 180404, value);
                break;
            default:
                break;
        }
    }

    /**
     * 元宝扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void deductGold(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        int value = ep.getDeductGold();
        addProgress(uid, 180503, value);
    }

    /**
     * 元宝增加事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addGold(GoldAddEvent event) {
        EPGoldAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        switch (way) {
            case YK:
                addProgress(uid, 180603, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            case JK:
                addProgress(uid, 180703, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            case RECEIVE_JK_AND_YK_AWARD:
                addProgress(uid, Arrays.asList(180603, 180703), TASK_PROGRESS_ADD_BASE_VALUE);
            default:
                break;
        }
    }

    /**
     * 铜钱增加事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        switch (way) {
            case MAOU_BOSS_FIGHT:
                addProgress(uid, 180205, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            default:
                break;
        }
    }

    /**
     * 卡牌升级事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addUserCardExp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        switch (way) {
            case LT:
                addProgress(uid, 180304, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            default:
                break;
        }
    }

    /**
     * 夺宝抽奖事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void snatchTreasureDraw(SnatchTreasureDrawEvent event) {
        EPSnatchTreasureDraw ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        int value = ep.getDrawTimes();
        switch (way) {
            case SNATCH_TREASUER_DRAW:
                addProgress(uid, 180403, value);
                break;
            default:
                break;
        }
    }

    /**
     * 村庄任务统计
     *
     * @param event
     */
    @Order
    @EventListener
    public void finishCunZTask(CunZTaskAchievedEvent event) {
        EPCunZTask ep = event.getEP();
        Long uid = ep.getGuId();
        addProgress(uid, 180704, TASK_PROGRESS_ADD_BASE_VALUE);
    }

    /**
     * 战斗胜利事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = ep.getFightType();
        Long uid = ep.getGuId();
        if (ep.getFightSubmit().getYeGuaiType() == YeGuaiEnum.YG_ELITE) {
            addProgress(uid, 180405, TASK_PROGRESS_ADD_BASE_VALUE);
        }
        switch (fightType) {
            case SXDH:
                addProgress(uid, 180505, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            case TRAINING:
                addProgress(uid, 180305, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            case FST:
                addProgress(uid, 180105, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            default:
                break;
        }
    }

    /**
     * 战斗失败事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void fightFail(CombatFailEvent event) {
        EPFightEnd source = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = source.getFightType();
        switch (fightType) {
            default:
                break;
        }
    }

    /**
     * 卡牌增加事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        if (WayEnum.KZ == way) {
            addProgress(uid, 180504, TASK_PROGRESS_ADD_BASE_VALUE);
        }
    }

    /**
     * 法宝扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        WayEnum way = ep.getWay();
        Integer treasureId = deductTreasure.getId();
        if (TreasureTool.getMapTreasureIds().contains(treasureId)) {
            addProgress(ep.getGuId(), 180203, deductTreasure.getNum());
        }
        switch (way) {
            case BYPALACE_REALIZATION:
                addProgress(ep.getGuId(), 180303, TASK_PROGRESS_ADD_BASE_VALUE);
                break;
            default:
                break;
        }
    }

    /**
     * 法宝增加事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        EVTreasure addTreasure = ep.getAddTreasures().get(0);
        WayEnum way = ep.getWay();
        int value = addTreasure.getNum();
        switch (way) {
            case HEIS:
                addProgress(ep.getGuId(), 180604, value);
                break;
            default:
                break;
        }
        if (addTreasure.getId() != TreasureEnum.XIU_XIAN_VALUE.getValue()) {
            return;
        }
        addProgress(ep.getGuId(), Arrays.asList(180901, 180902, 180903, 180904, 180905), value);

    }

    /**
     * 行会任务完成事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void finishGuildEightDiagramsTask(GuildTaskFinishedEvent event) {
        EPGuildTaskFinished ep = event.getEP();
        Long uid = ep.getGuId();
        addProgress(uid, 180104, TASK_PROGRESS_ADD_BASE_VALUE);
    }

    /**
     * 商帮任务完成事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void finishBusinessGangTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask ep = event.getEP();
        if (ep.getTaskGroup() == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            return;
        }
        addProgress(ep.getGuId(), 180705, TASK_PROGRESS_ADD_BASE_VALUE);
    }

    /**
     * 独立魔王击杀事件
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void killAloneMaou(AloneMaouKilledEvent event) {
        EPAloneMaou ep = event.getEP();
        AloneMaouLevelInfo maouLevelInfo = ep.getMaouLevelInfo();
        addProgress(ep.getGuId(), 180605, TASK_PROGRESS_ADD_BASE_VALUE);

    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param taskId
     * @param value
     */
    private void addProgress(long uid, int taskId, int value) {
        if (!bigGodPlanProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        UserBigGodPlanTask userBigGodPlanTask = bigGodPlanTaskDataService.getBigGodPlanTaskFromCache(uid, taskId);
        if (null == userBigGodPlanTask) {
            return;
        }
        int openDays = bigGodPlanTaskService.getOpenDays(uid);
        //是否当天登录事件
        boolean isTodayLoginTask = LOGIN_TASK_ID.contains(userBigGodPlanTask.getTaskId()) && openDays != userBigGodPlanTask.getDays();
        if (isTodayLoginTask) {
            return;
        }
        //是否已开放任务天数
        boolean isOPenTaskDays = openDays < userBigGodPlanTask.getDays();
        if (isOPenTaskDays) {
            return;
        }
        Integer status = userBigGodPlanTask.getStatus();
        //是否任务已完成
        boolean isAccomplishedTask = status >= TaskStatusEnum.ACCOMPLISHED.getValue();
        if (isAccomplishedTask) {
            return;
        }
        userBigGodPlanTask.addValue(value);
        bigGodPlanTaskDataService.updateBigGodPlanTaskToCache(uid, userBigGodPlanTask);
        redNotice(uid, taskId, userBigGodPlanTask);
    }

    /**
     * 增加多个任务进度
     *
     * @param uid
     * @param taskIds
     * @param value
     */
    private void addProgress(long uid, List<Integer> taskIds, int value) {
        for (Integer taskId : taskIds) {
            addProgress(uid, taskId, value);
        }
    }

    /**
     * 发送红点
     *
     * @param uid
     * @param taskId
     * @param userBigGodPlanTask
     */
    private void redNotice(long uid, int taskId, UserBigGodPlanTask userBigGodPlanTask) {
        if (userBigGodPlanTask.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            int days = userBigGodPlanTask.getDays() == 0 ? MAX_DAYS : userBigGodPlanTask.getDays();
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.BIG_GOD_PLAN, days, taskId);
        }
    }
}
