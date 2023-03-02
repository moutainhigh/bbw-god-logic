package com.bbw.god.notify.rednotice;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.common.AccomplishEvent;
import com.bbw.god.event.common.EPAccomplish;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementTypeEnum;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import com.bbw.god.rd.RDCommon;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description 红点通知监听
 * @date 2020-02-06 14:08
 **/
@Component
public class RedNoticeListener {
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private RedNoticeService redNoticeService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;

    private static final List<Integer> DRAW_TREASURES = Arrays.asList(TreasureEnum.JU_XIAN_LING.getValue(),
            TreasureEnum.XZY.getValue(), TreasureEnum.JZY.getValue(), TreasureEnum.MZY.getValue(),
            TreasureEnum.SZY.getValue(), TreasureEnum.HZY.getValue(), TreasureEnum.TZY.getValue());

    @EventListener
    public void firstLoginNotice(FirstLoginPerDayEvent event) {
//        EPFirstLoginPerDay ep = event.getEP();
//        Long uid = ep.getGuId();
//        RDGameUser rd = ep.getRdGameUser();
//        rd.addRedNotices(redNoticeService.getPerDayFirstLoginNotice(uid));
    }

    @EventListener
    @Async
    public void accomplish(AccomplishEvent event) {
        EPAccomplish ep = event.getEP();
        long uid = ep.getGuId();
        ModuleEnum module = ep.getModule();
        switch (module) {
            case ACHIEVEMENT:
                int accomplishNum = this.userAchievementService.getAbleAward(uid,
                        AchievementTypeEnum.fromValue(ep.getType()));
                this.m2cService.sendRedNotice(uid, Arrays.asList(this.redNoticeService.buildNoticeData3(module,
                        ep.getType(), accomplishNum)));
                break;
            case TASK:
                sendTaskNotice(uid, ep.getType());
                break;
            case WANXIAN:
                this.m2cService.sendRedNotice(uid, redNoticeService.getWanXianNotice(uid));
                break;
        }
    }

    @EventListener
    @Order(2)
    public void addCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        Long uid = ep.getGuId();
        // 非原世界城池直接return
        if (ep.getValue().isNightmare()) {
            return;
        }
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.MULTIPLE_REBATE);
        // 活动不存在
        if (null == a) {
            return;
        }
        UserAttackCityRedNotice redNotice = gameUserService.getSingleItem(uid, UserAttackCityRedNotice.class);
        if (null == redNotice) {
            redNotice = UserAttackCityRedNotice.getInstance(uid);
            gameUserService.addItem(uid, redNotice);
        }
        EPCityAdd epCityAdd = ep.getValue();
        int cityId = epCityAdd.getCityId();
        CfgCityEntity city = CityTool.getCityById(cityId);
        int cityLevel = city.getLevel();
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.MULTIPLE_REBATE);
        List<UserActivity> uas = this.activityService.getUserActivities(uid, a.gainId(), ActivityEnum.MULTIPLE_REBATE);
        UserAttackCityRedNotice finalNotice = redNotice;
        uas = uas.stream().filter(tmp -> tmp.getStatus() == AwardStatus.ACHIEVED.getValue() &&
                !finalNotice.ifNotice(cityLevel, tmp.getBaseId())).collect(Collectors.toList());
        for (UserActivity ua : uas) {
            redNotice.notice(ua.getBaseId(), cityLevel);
        }
        RDCommon rd = ep.getRd();
        rd.getRedNotices().add(redNoticeService.buildNoticeData3(ModuleEnum.ACTIVITY, ActivityEnum.MULTIPLE_REBATE.getValue(), uas.size()));
        gameUserService.updateItem(redNotice);
    }

    private void sendTaskNotice(long uid, int taskType) {
        List<String> taskNotices = new ArrayList<>();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.fromValue(taskType);
        switch (taskTypeEnum) {
            case DAILY_TASK:
                taskNotices.add(this.redNoticeService.getDailyTaskNotice(uid));
                break;
            case GOD_TRAINING_TASK:
                GameUser gu = gameUserService.getGameUser(uid);
                taskNotices.addAll(this.redNoticeService.getGodTrainingTaskNotice(gu));
                break;
            case MAIN_TASK:
                taskNotices.add(this.redNoticeService.getMainTaskNotice(uid));
                break;
            case SXDH_SEASON_TASK:
                taskNotices.add(this.redNoticeService.getSxdhSeasonTaskNotice(uid));
                break;
            case GUESS_DAILY_TASK:
                taskNotices.add(this.redNoticeService.getActivityDailyTaskNotice(uid));
                break;
        }
        if (ListUtil.isNotEmpty(taskNotices)) {
            this.m2cService.sendRedNotice(uid, taskNotices);
        }
    }

    @EventListener
    @Async
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        List<Integer> treasureIds = ep.getAddTreasures().stream().map(EVTreasure::getId).collect(Collectors.toList());
        // 没有交集，说明获得的法宝没有抽卡专用的道具
        if (Collections.disjoint(treasureIds, DRAW_TREASURES)) {
            return;
        }
        List<String> notices = this.redNoticeService.getCardPoolNotice(ep.getGuId());
        if (ListUtil.isNotEmpty(notices)) {
            this.m2cService.sendRedNotice(ep.getGuId(), notices);
        }
    }
}