package com.bbw.god.gameuser.task.brocadegift;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.ConsumeType;
import com.bbw.god.activity.holiday.lottery.HolidayLotteryType;
import com.bbw.god.activity.holiday.lottery.event.EPHolidayLotteryDraw;
import com.bbw.god.activity.holiday.lottery.event.HolidayLotteryDrawEvent;
import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftProcessor;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.mall.event.EPMallBuy;
import com.bbw.god.mall.event.MallBuyEvent;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 锦礼每天任务监听器
 *
 * @author: huanghb
 * @date: 2022/2/15 16:39
 */
@Slf4j
@Component
@Async
public class BrocadeGiftDailyTaskListener {
    @Autowired
    private BrocadeGiftDailyTaskDataService brocadeGiftDailyTaskDataService;
    @Autowired
    private BrocadeGiftDailyTaskService brocadeGiftDailyTaskService;
    @Autowired
    private HolidayBrocadeGiftProcessor holidayBrocadeGiftProcessor;
    @Autowired
    private GameUserService gameUserService;
    /** 每日任务id */
    private static final List<Integer> DAILY_TASK_IDS = Arrays.asList(190103, 190105, 190109, 190110, 190111);
    /** 登录任务id */
    private static final List<Integer> LOGIN_TASK_IDS = Arrays.asList(190201, 190202);
    /** 翻牌卡道具消耗任务id */
    private static final List<Integer> FLOP_CARD_TASK_IDS = Arrays.asList(190206, 190207, 190208);
    /** 天灯道具获得任务id */
    private static final List<Integer> SKY_LANTERN_TASK_IDS = Arrays.asList(190106, 190107, 190108);
    /** 活动获得道具id */
    private static final List<Integer> FLOP_CARD_IDS = Arrays.asList(
            TreasureEnum.TREASURE_MAP_FLOP_CARD.getValue());
    /** 五灵珠道具获得任务id */
    private static final List<Integer> FIVE_LING_ZHU_TASK_IDS = Arrays.asList(190203, 190204, 190205);
    /** 五气朝元抽奖任务id */
    private static final List<Integer> DRAW_TASK_IDS = Arrays.asList(190109, 190110, 190111);
    /** 活动获得五灵珠道具id */
    private static final List<Integer> FIVE_LING_ZHU_IDS = Arrays.asList(
            TreasureEnum.JYZ.getValue(),
            TreasureEnum.MYZ.getValue(),
            TreasureEnum.SYZ.getValue(),
            TreasureEnum.HYZ.getValue(),
            TreasureEnum.TYZ.getValue());
    /** 任务基础增加进度 */
    private static final int TASK_PROGRESS_BASE_ADD_PROGRESS = 1;
    /** 商铺内含元宝消费项目的物品类别 */
    private static final List<MallEnum> GOLD_CONSUME_MALL_ENUM_LIST = Arrays.asList(
            MallEnum.SM,
            MallEnum.THLB,
            MallEnum.DJ,
            MallEnum.SNATCH_TREASURE,
            MallEnum.EMOTICON,
            MallEnum.HOLIDAY_MALL_LIMIT_PACK
    );


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
        cleanProgress(uid, DAILY_TASK_IDS);
        addProgress(uid, LOGIN_TASK_IDS, TASK_PROGRESS_BASE_ADD_PROGRESS);
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
        Integer treasureId = addTreasure.getId();
        int addProgress = addTreasure.getNum();
        Long uid = ep.getGuId();
//        if (TreasureEnum.SKY_LANTERN.getValue() == treasureId) {
//            addProgress(uid, SKY_LANTERN_TASK_IDS, addProgress);
//        }
        if (FIVE_LING_ZHU_IDS.contains(treasureId)) {
            addProgress(uid, FIVE_LING_ZHU_TASK_IDS, addProgress);
        }
    }

    /**
     * 法宝扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure addTreasure = ep.getDeductTreasure();
        Integer treasureId = addTreasure.getId();
        int addProgress = addTreasure.getNum();
        Long uid = ep.getGuId();
//        if (FLOP_CARD_IDS.contains(treasureId)) {
//            addProgress(uid, FLOP_CARD_TASK_IDS, addProgress);
//        }
    }

    /**
     * 五气朝元抽奖事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void holidayLotteryDraw(HolidayLotteryDrawEvent event) {
        EPHolidayLotteryDraw ep = event.getEP();
        if (ep.getLotteryType() != HolidayLotteryType.WQCY) {
            return;
        }
        addProgress(ep.getGuId(), DRAW_TASK_IDS, TASK_PROGRESS_BASE_ADD_PROGRESS);
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
        addProgress(uid, 190103, value);
    }

    /**
     * 商城购买
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void mallBuy(MallBuyEvent event) {
        EPMallBuy ep = event.getEP();
        Long uid = ep.getGuId();
        CfgMallEntity mall = MallTool.getMall(ep.getMallType(), ep.getGoodsId());
        if (ConsumeType.GOLD.getValue() != mall.getUnit()) {
            return;
        }
        MallEnum mallEnum = MallEnum.fromValue(ep.getMallType());
        if (GOLD_CONSUME_MALL_ENUM_LIST.contains(mallEnum)) {
            addProgress(uid, 190105, TASK_PROGRESS_BASE_ADD_PROGRESS);
        }
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
        if (fightType == FightTypeEnum.ZXZ) {
            addProgress(uid, 190104, TASK_PROGRESS_BASE_ADD_PROGRESS);
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
        EPFightEnd ep = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = ep.getFightType();
        Long uid = ep.getGuId();
        if (fightType == FightTypeEnum.ZXZ) {
            addProgress(uid, 190104, TASK_PROGRESS_BASE_ADD_PROGRESS);
        }
    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param taskId
     * @param value
     */
    private void addProgress(long uid, int taskId, int value) {
        if (!holidayBrocadeGiftProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        List<UserBrocadeGiftDailyTask> userBrocadeGiftDailyTasks = brocadeGiftDailyTaskService.getCurUseraBrocadeGiftDailyTasks(uid);
        if (ListUtil.isEmpty(userBrocadeGiftDailyTasks)) {
            return;
        }
        UserBrocadeGiftDailyTask userBrocadeGiftDailyTask = userBrocadeGiftDailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
        if (null == userBrocadeGiftDailyTask) {
            return;
        }
        //当天登录任务事件已完成
        boolean isAccomplishTodayLoginTask = isAccomplishTodayLoginTask(userBrocadeGiftDailyTask);
        if (isAccomplishTodayLoginTask) {
            return;
        }
        Integer status = userBrocadeGiftDailyTask.getStatus();
        //是否任务已完成
        boolean isAccomplishedTask = status >= TaskStatusEnum.ACCOMPLISHED.getValue();
        if (isAccomplishedTask) {
            return;
        }
        userBrocadeGiftDailyTask.addProgress(value);
        brocadeGiftDailyTaskDataService.updateBrocadeGiftDailyTaskToCache(uid, userBrocadeGiftDailyTask);
        redNotice(uid, taskId, userBrocadeGiftDailyTask);
    }

    /**
     * 是否完成今日登录任务
     *
     * @param userBrocadeGiftDailyTask
     * @return
     */
    private boolean isAccomplishTodayLoginTask(UserBrocadeGiftDailyTask userBrocadeGiftDailyTask) {
        //是否登录任务
        if (!LOGIN_TASK_IDS.contains(userBrocadeGiftDailyTask.getTaskId())) {
            return false;
        }
        //今天是否完成过该任务
        if (userBrocadeGiftDailyTask.getAccomplishTime() == 0) {
            return false;
        }
        Date taskAccomplishTime = DateUtil.fromDateLong(userBrocadeGiftDailyTask.getAccomplishTime());
        boolean isAccomplishTodayTask = DateUtil.isToday(taskAccomplishTime);
        if (!isAccomplishTodayTask) {
            return false;
        }
        return true;
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
     * 增加多个任务进度
     *
     * @param uid
     * @param taskIds
     */
    private void cleanProgress(long uid, List<Integer> taskIds) {
        for (Integer taskId : taskIds) {
            cleanProgress(uid, taskId);
        }
    }

    private void cleanProgress(long uid, int taskId) {
        if (!holidayBrocadeGiftProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        List<UserBrocadeGiftDailyTask> userBrocadeGiftDailyTasks = brocadeGiftDailyTaskService.getCurUseraBrocadeGiftDailyTasks(uid);
        if (ListUtil.isEmpty(userBrocadeGiftDailyTasks)) {
            return;
        }
        UserBrocadeGiftDailyTask userBrocadeGiftDailyTask = userBrocadeGiftDailyTasks.stream().filter(tmp -> tmp.getTaskId() == taskId).findFirst().orElse(null);
        if (null == userBrocadeGiftDailyTask) {
            return;
        }
        boolean isTodaytask = isTodaytask(userBrocadeGiftDailyTask);
        if (isTodaytask) {
            return;
        }
        userBrocadeGiftDailyTask.cleanProgress();
        brocadeGiftDailyTaskDataService.updateBrocadeGiftDailyTaskToCache(uid, userBrocadeGiftDailyTask);
    }

    /**
     * 是否今天任务
     *
     * @param userBrocadeGiftDailyTask
     * @return
     */
    private boolean isTodaytask(UserBrocadeGiftDailyTask userBrocadeGiftDailyTask) {
        Date taskGenerateTime = DateUtil.fromDateLong(userBrocadeGiftDailyTask.getGenerateTime());
        return DateUtil.isToday(taskGenerateTime);
    }

    /**
     * 发送红点
     *
     * @param uid
     * @param taskId
     * @param userBrocadeGiftDailyTask
     */
    private void redNotice(long uid, int taskId, UserBrocadeGiftDailyTask userBrocadeGiftDailyTask) {
        if (userBrocadeGiftDailyTask.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.TASK, TaskTypeEnum.ANNUAL_GIFT_DAILY_TASK.getValue(), taskId);
        }
    }
}
