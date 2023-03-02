package com.bbw.god.activity.holiday;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.HolidayTaskTool;
import com.bbw.god.activity.holiday.processor.HolidayDailyTaskProcessor;
import com.bbw.god.activity.holiday.processor.HolidaySpecialTaskProcessor;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日任务监听器
 * @date 2020/9/2 10:04
 **/
@Component
public class HolidayTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private HolidayDailyTaskProcessor dailyTaskProcessor;
    @Autowired
    private HolidaySpecialTaskProcessor specialTaskProcessor;

    @Async
    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd epFightEnd = (EPFightEnd) event.getSource();
        Long uid = epFightEnd.getGuId();
        FightTypeEnum fightType = epFightEnd.getFightType();
        if (FightTypeEnum.YG != fightType) {
            return;
        }
        addTaskProgress(uid, Arrays.asList(1010, 1020, 1030), 1);
    }

/*    @Async
    @EventListener
    public void fightFail(FightFailEvent event) {
        EVFightEnd evFightEnd = (EVFightEnd) event.getSource();
        Long uid = evFightEnd.getGuId();
        FightTypeEnum fightType = evFightEnd.getFightType();
        switch (fightType) {
            case FST:
                addDailyTaskProgress(uid, Collections.singletonList(1090), 1);
                break;
            case SXDH:
                addDailyTaskProgress(uid, Collections.singletonList(1100), 1);
                break;
        }
    }*/

/*    @Async
    @EventListener
    public void eliteYeGuaiFightWin(EliteYeGuaiFightWinEvent event) {
        EPEliteYeGuaiFightWin ep = event.getEP();
        Long uid = ep.getGuId();
        addDailyTaskProgress(uid, Collections.singletonList(1110), 1);
    }*/

    @Async
    @EventListener
    public void deductSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        if (WayEnum.TRADE != ep.getWay()) {
            return;
        }
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        Long uid = ep.getGuId();
        //addDailyTaskProgress(uid, Collections.singletonList(1120), specialInfoList.size());
        long count = specialInfoList.stream().filter(tmp -> SpecialTool.getSpecialById(tmp.getBaseSpecialIds()).isSyntheticSpecialty()).count();
        addSpecialTaskProgress(uid, Arrays.asList(2120, 2130, 2140, 2150, 2160, 2170), (int) count);
    }

    @Async
    @EventListener
    public void finishCocTask(CocTaskFinishedEvent event) {
        EPTaskFinished ep = event.getEP();
        Long uid = ep.getGuId();
        addTaskProgress(uid, Arrays.asList(1040, 1050, 1060), 1);
    }

/*    @Async
    @EventListener
    public void holidayDraw(HolidayLotteryDrawEvent event) {
        EPHolidayLotteryDraw ep = event.getEP();
        Integer resultLevel = ep.getResultLevel();
        HolidayLotteryService20.ResultLevel level = HolidayLotteryService20.ResultLevel.fromValue(resultLevel);
        if (null == level) {
            return;
        }
        int taskId;
        switch (level) {
            case ZY:
                taskId = 2110;
                break;
            case BY:
                taskId = 2100;
                break;
            case TH:
                taskId = 2090;
                break;
            case JS:
                taskId = 2080;
                break;
            case JR:
                taskId = 2070;
                break;
            case XC:
                taskId = 2060;
                break;
            default:
                return;
        }
        addSpecialTaskProgress(ep.getGuId(), Collections.singletonList(taskId), 1);
    }*/

    private void addTaskProgress(long uid, List<Integer> taskIds, int value) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_DAILY_TASK);
        if (a == null) {
            return;
        }
        List<UserActivity> userActivities = dailyTaskProcessor.getUserActivities(uid, a);
        userActivities = userActivities.stream().filter(uc -> taskIds.contains(uc.getBaseId())).collect(Collectors.toList());
        for (UserActivity userActivity : userActivities) {
            addTaskProgress(userActivity, value);
        }
        gameUserService.updateItems(userActivities);
    }

    private void addSpecialTaskProgress(long uid, List<Integer> taskIds, int value) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_SPECIAL_TASK);
        if (a == null) {
            return;
        }
        List<UserActivity> userActivities = specialTaskProcessor.getUserActivities(uid, a);
        userActivities = userActivities.stream().filter(uc -> taskIds.contains(uc.getBaseId())).collect(Collectors.toList());
        for (UserActivity userActivity : userActivities) {
            addTaskProgress(userActivity, value);
        }
        gameUserService.updateItems(userActivities);
    }

    /**
     * 增加活动进度
     *
     * @param userActivity
     * @param value
     */
    private void addTaskProgress(UserActivity userActivity, int value) {
        if (userActivity.getStatus() >= AwardStatus.ENABLE_AWARD.getValue()) {
            return;
        }
        userActivity.setProgress(userActivity.getProgress() + value);
        Integer needValue = HolidayTaskTool.getTaskById(userActivity.getBaseId()).getValue();
        if (userActivity.getProgress() >= needValue) {
            userActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
            userActivity.setProgress(needValue);
        }
    }
}
