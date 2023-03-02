package com.bbw.god.gameuser.task.halloweenRestaurant;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.HolidayHalloweenRestaurantProcessor;
import com.bbw.god.city.event.CityArriveEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.treasure.event.*;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.bbw.god.gameuser.task.halloweenRestaurant.HalloweenRestaurantDailyTaskService.*;

/**
 * 万圣餐厅每天任务监听器
 *
 * @author: huanghb
 * @date: 2022/10/14 9:18
 */
@Slf4j
@Component
@Async
public class HalloweenRestaurantDailyTaskListener {
    @Autowired
    private HalloweenRestaurantDailyTaskDataService halloweenRestaurantDailyTaskDataService;
    @Autowired
    private HolidayHalloweenRestaurantProcessor holidayHalloweenRestaurantProcessor;
    @Autowired
    private HalloweenRestaurantDailyTaskService halloweenRestaurantDailyTaskService;
    @Autowired
    private GameUserService gameUserService;
    /** 任务基础增加进度 */
    private static final int TASK_PROGRESS_BASE_ADD_PROGRESS = 1;
    /** 商铺内含元宝消费项目的物品类别 */
    private static final List<WayEnum> GOLD_CONSUME_MALL_ENUM_LIST = Arrays.asList(
            WayEnum.MALL_BUY
    );


    /**
     * 登录事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void login(FirstLoginPerDayEvent event) {
        Long uid = event.getEP().getUid();
        cleanProgress(uid, DAILY_COMPOUND_TASK_IDS);
        cleanProgress(uid, DAILY_GOLD_TASK_IDS);
        cleanProgress(uid, DAILY_USE_TREASURE_TASK_IDS);
    }

    @Order(2)
    @EventListener
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();

        if (WayEnum.HALLOWEEN_RESTAURANT_SYNTHETIC != way) {
            return;
        }
        addProgress(uid, DAILY_COMPOUND_TASK_IDS, TASK_PROGRESS_BASE_ADD_PROGRESS);
    }

    /**
     * 村庄到达事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void cunZArrive(CityArriveEvent event) {
        EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
        CfgRoadEntity road = RoadTool.getRoadById(ep.getValue());
        CfgCityEntity city = road.getCity();
        if (CityTypeEnum.CZ.getValue() != city.getType()) {
            return;
        }
        addProgress(ep.getGuId(), 230013, TASK_PROGRESS_BASE_ADD_PROGRESS);
    }

    /**
     * 特产扣除事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void specialDeduct(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        if (WayEnum.TRADE != ep.getWay()) {
            return;
        }
        addProgress(ep.getGuId(), 230014, ep.getSpecialInfoList().size());
    }

    /**
     * 战胜事件
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd source = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = source.getFightType();
        Long uid = source.getGuId();
        if (FightTypeEnum.YG == fightType) {
            addProgress(uid, 230009, TASK_PROGRESS_BASE_ADD_PROGRESS);
        }
        if (FightTypeEnum.TRAINING == fightType) {
            addProgress(uid, 230010, TASK_PROGRESS_BASE_ADD_PROGRESS);

        }
    }

    /**
     * 商城购买
     *
     * @param event
     */
    @EventListener
    @Order(1000)
    public void mallBuy(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        WayEnum wayEnum = WayEnum.fromValue(ep.getWay().getValue());
        if (GOLD_CONSUME_MALL_ENUM_LIST.contains(wayEnum)) {
            addProgress(uid, DAILY_GOLD_TASK_IDS, ep.getDeductGold());
        }
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
        addProgress(uid, 230015, value);
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
        Integer treasureId = deductTreasure.getId();
        if (TreasureTool.getMapTreasureIds().contains(treasureId)) {
            addProgress(ep.getGuId(), 230011, deductTreasure.getNum());
        }
        if (TreasureTool.getFightTreasureIds().contains(treasureId)) {
            addProgress(ep.getGuId(), 230012, deductTreasure.getNum());
        }
    }

    /**
     * 完成商帮任务统计
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void finishGangTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask ep = event.getEP();
        addProgress(ep.getGuId(), 230016, TASK_PROGRESS_BASE_ADD_PROGRESS);


    }

    /**
     * 增加任务进度
     *
     * @param uid
     * @param taskId
     * @param value
     */
    private void addProgress(long uid, int taskId, int value) {
        if (!holidayHalloweenRestaurantProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        UserHalloweenRestaurantDailyTask dailyTask = halloweenRestaurantDailyTaskService.getCurUserDailyTask(uid,taskId);
        if (null == dailyTask) {
            return;
        }
        Integer status = dailyTask.getStatus();
        //是否每日一次任务
        List<Integer> dailyOneTimesTask = new ArrayList<>();
        dailyOneTimesTask.addAll(DAILY_COMPOUND_TASK_IDS);
        dailyOneTimesTask.addAll(DAILY_GOLD_TASK_IDS);
        boolean isDailyOneTimesTask = dailyOneTimesTask.contains(dailyTask.getTaskId());
        //是否每日任务已完成
        boolean isAccomplishedDailyTask = status >= TaskStatusEnum.ACCOMPLISHED.getValue() && isDailyOneTimesTask;
        if (isAccomplishedDailyTask) {
            return;
        }
        CfgTaskEntity cfgTaskEntity = TaskTool.getDailyTask(taskId);
        //是否每日使用法宝任务完成次数达到上限
        boolean isUseTreasureTaskTimesLimit = dailyTask.getProgress() / cfgTaskEntity.getValue() > USE_TREASURE_TIMES_LIMIT;
        if (isUseTreasureTaskTimesLimit && DAILY_USE_TREASURE_TASK_IDS.contains(taskId)) {
            return;
        }
        dailyTask.addProgress(value);
        halloweenRestaurantDailyTaskDataService.updateDailyTaskToCache(uid, dailyTask);
        redNotice(uid, taskId, dailyTask);
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
        if (!holidayHalloweenRestaurantProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        UserHalloweenRestaurantDailyTask task = halloweenRestaurantDailyTaskDataService.getDailyTaskFromCache(uid, taskId);
        if (null == task) {
            return;
        }
        boolean isTodaytask = isTodaytask(task);
        if (isTodaytask) {
            return;
        }
        task.cleanProgress();
        halloweenRestaurantDailyTaskDataService.updateDailyTaskToCache(uid, task);
    }

    /**
     * 是否今天任务
     *
     * @param task
     * @return
     */
    private boolean isTodaytask(UserHalloweenRestaurantDailyTask task) {
        Date taskGenerateTime = DateUtil.fromDateLong(task.getGenerateTime());
        return DateUtil.isToday(taskGenerateTime);
    }

    /**
     * 发送红点
     *
     * @param uid
     * @param taskId
     * @param task
     */
    private void redNotice(long uid, int taskId, UserHalloweenRestaurantDailyTask task) {
        if (task.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.TASK, TaskTypeEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK.getValue(), taskId);
        }
    }
}
