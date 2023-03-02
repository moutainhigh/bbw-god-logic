package com.bbw.god.gameuser.task.activitytask.guesstask;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.processor.WorldCupGuessTaskProcessor;
import com.bbw.god.city.event.CityArriveEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.task.GuildTaskStatistic;
import com.bbw.god.gameuser.statistic.behavior.task.GuildTaskStatisticService;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.activitytask.ActivityDailyTaskDataService;
import com.bbw.god.gameuser.task.activitytask.ActivityDailyTaskService;
import com.bbw.god.gameuser.task.activitytask.UserActivityDailyTask;
import com.bbw.god.gameuser.task.businessgang.UserBusinessGangWeeklyTask;
import com.bbw.god.gameuser.task.businessgang.UserWeeklyTaskService;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.pay.DeliverNotifyEvent;
import com.bbw.god.pay.ProductService;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 竞猜每天任务监听器
 *
 * @author: huanghb
 * @date: 2022/11/14 10:15
 */
@Slf4j
@Component
@Async
public class GuessDailyTaskListener {
    @Autowired
    private ActivityDailyTaskDataService activityDailyTaskDataService;
    @Autowired
    private WorldCupGuessTaskProcessor worldCupGuessTaskProcessor;
    @Autowired
    private GuessDailyTaskService guessDailyTaskService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    GuildTaskStatisticService guildTaskStatisticService;
    @Autowired
    ActivityDailyTaskService activityDailyTaskService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;
    @Autowired
    private UserWeeklyTaskService userWeeklyTaskService;
    /** 任务基础增加进度 */
    private static final int TASK_PROGRESS_BASE_ADD_PROGRESS = 1;

    /**
     * 充值事件
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void recharge(DeliverNotifyEvent event) {
        UserReceipt userReceipt = event.getParam();
        long uid = userReceipt.getGameUserId();
        CfgProductGroup.CfgProduct product = this.productService.getCfgProduct(userReceipt.getProductId());
        int price = product.getPrice();
        if (!productService.ifPayForDiamond(userReceipt.getProductId())) {
            return;
        }
        guessDailyTaskService.addProgress(uid, 240001, price);
    }

    /**
     * 元宝扣除事件
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void goldDeductEvent(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        long uid = ep.getGuId();
        guessDailyTaskService.addProgress(uid, 240002, ep.getDeductGold());
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
        guessDailyTaskService.addProgress(ep.getGuId(), 240003, TASK_PROGRESS_BASE_ADD_PROGRESS);


    }

    /**
     * 行会任务完成
     *
     * @param event
     */
    @Order(2)
    @EventListener
    public void finishedGuidTask(GuildTaskFinishedEvent event) {

        long uid = event.getEP().getGuId();
        guessDailyTaskService.addProgress(uid, 240004, TASK_PROGRESS_BASE_ADD_PROGRESS);
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
        guessDailyTaskService.addProgress(ep.getGuId(), 240005, TASK_PROGRESS_BASE_ADD_PROGRESS);
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
            guessDailyTaskService.addProgress(uid, 240006, TASK_PROGRESS_BASE_ADD_PROGRESS);
        }
        if (FightTypeEnum.TRAINING == fightType) {
            guessDailyTaskService.addProgress(uid, 240007, TASK_PROGRESS_BASE_ADD_PROGRESS);

        }
    }

    @EventListener
    @Order(1000)
    public void login(LoginEvent event) {
        LoginPlayer ep = event.getLoginPlayer();
        long uid = ep.getUid();

        guessDailyTaskService.addProgress(uid, 240008, TASK_PROGRESS_BASE_ADD_PROGRESS);
        if (!DateUtil.isToday(DateUtil.fromDateInt(20221120))) {
            return;
        }
        //获取行会任务完成统计
        GuildTaskStatistic statistic = guildTaskStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        UserActivityDailyTask guildDailyTask = activityDailyTaskService.getCurUserDailyTask(uid, 240004);
        //需要修复行会任务次数
        int needFixGuildTimes = statistic.getToday() - guildDailyTask.gainTotalTimesOfAccomplish();
        if (needFixGuildTimes > 0) {
            guessDailyTaskService.addProgress(uid, 240004, needFixGuildTimes);
        }
        //获得商帮任务完成次数
        UserBusinessGangTaskInfo businessGangTaskInfo = userBusinessGangService.getOrCreateUserBusinessGangTask(uid);
        int businessGangTaskNum = businessGangTaskInfo.getTotalAwardableNum() - businessGangTaskInfo.getAwardableNum();
        List<UserBusinessGangWeeklyTask> weeklyTask = userWeeklyTaskService.getAllTasks(uid).stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.AWARDED.getValue()).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(weeklyTask)) {
            businessGangTaskNum += 1;
        }
        UserActivityDailyTask businessGangDailyTask = activityDailyTaskService.getCurUserDailyTask(uid, 240003);
        //需要修复商帮进度
        int needFixBusinessGangTimes = businessGangTaskNum - businessGangDailyTask.gainTotalTimesOfAccomplish();
        if (needFixBusinessGangTimes > 0) {
            guessDailyTaskService.addProgress(uid, 240003, needFixBusinessGangTimes);
        }
    }

}
