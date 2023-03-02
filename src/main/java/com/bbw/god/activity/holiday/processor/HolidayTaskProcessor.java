package com.bbw.god.activity.holiday.processor;

import com.bbw.common.ListUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayTaskEntity;
import com.bbw.god.activity.holiday.config.HolidayTaskTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日任务处理器
 * @date 2020/8/26 17:32
 **/
public abstract class HolidayTaskProcessor extends AbstractActivityProcessor {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private SyncLockUtil syncLockUtil;

    public HolidayTaskProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_DAILY_TASK, ActivityEnum.HOLIDAY_SPECIAL_TASK);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    public RDCommon receiveTaskAward(long uid, int sid, int taskId, int activityType) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        UserActivity userActivity = getUserActivity(uid, a, taskId);
        // 已领取
        if (AwardStatus.AWARDED.getValue() == userActivity.getStatus()) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        // 未达成
        if (AwardStatus.UNAWARD.getValue() == userActivity.getStatus()) {
            throw new ExceptionForClientTip("activity.not.finish");
        }
        userActivity.setStatus(AwardStatus.AWARDED.getValue());
        gameUserService.updateItem(userActivity);

        RDCommon rd = new RDCommon();
        CfgHolidayTaskEntity taskEntity = HolidayTaskTool.getTaskById(taskId);
        List<Award> awards = taskEntity.getAwards();
        awardService.fetchAward(uid, awards, WayEnum.HOLIDAY_TASK, "", rd);

        return rd;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDActivityList rd = new RDActivityList();
        List<RDActivityItem> rdActivities = new ArrayList<>();
        List<UserActivity> userActivities = getUserActivities(uid, a);
        for (UserActivity userActivity : userActivities) {
            RDActivityItem rdActivity = getRDActivity(userActivity);
            rdActivities.add(rdActivity);
        }
        rd.setItems(rdActivities);
        return rd;
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        RDActivityList activities = getActivities(gu.getId(), a.gainType());
        long count = activities.getItems().stream().filter(tmp -> tmp.getStatus().equals(AwardStatus.ENABLE_AWARD.getValue())).count();
        return (int) count;
    }

    /**
     * 封装数据
     *
     * @param userActivity
     * @return
     */
    private RDActivityItem getRDActivity(UserActivity userActivity) {
        CfgHolidayTaskEntity cfgTask = HolidayTaskTool.getTaskById(userActivity.getBaseId());
        RDActivityItem rdActivity = new RDActivityItem();
        rdActivity.setId(userActivity.getBaseId());
        rdActivity.setTotalProgress(cfgTask.getValue());
        rdActivity.setProgress(userActivity.getProgress());
        rdActivity.setStatus(userActivity.getStatus());
        List<Award> awards = cfgTask.getAwards();
        rdActivity.setAwards(awards);
        return rdActivity;
    }

    /**
     * 获取玩家节日任务实例
     *
     * @param uid
     * @param a
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UserActivity> getUserActivities(long uid, IActivity a) {
        List<UserActivity> userActivities = activityService.getUserActivities(uid).stream().filter(uc ->
                uc.getAId().equals(a.gainId())).collect(Collectors.toList());
        // 不存在就生成
        if (ListUtil.isEmpty(userActivities)) {
            userActivities = (List<UserActivity>) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                List<UserActivity> dataList = activityService.getUserActivities(uid).stream().filter(uc ->
                        uc.getAId().equals(a.gainId())).collect(Collectors.toList());
                if (ListUtil.isEmpty(dataList)) {
                    dataList = new ArrayList<>();
                    List<CfgHolidayTaskEntity> dailyTasks = getHolidayTask();
                    for (CfgHolidayTaskEntity dailyTask : dailyTasks) {
                        UserActivity userActivity = UserActivity.instance(uid, a.gainId(), 0, AwardStatus.UNAWARD.getValue(),
                                dailyTask.getId(), dailyTask.getName());
                        dataList.add(userActivity);
                    }
                    gameUserService.addItems(dataList);
                }
                return dataList;
            });
        }
        return userActivities;
    }

    public UserActivity getUserActivity(long uid, IActivity a, int taskId) {
        List<UserActivity> userActivities = getUserActivities(uid, a);
        return userActivities.stream().filter(ua -> ua.getBaseId() == taskId).findFirst().orElse(null);
    }

    /**
     * 获取节日任务
     *
     * @return
     */
    protected abstract List<CfgHolidayTaskEntity> getHolidayTask();
}
