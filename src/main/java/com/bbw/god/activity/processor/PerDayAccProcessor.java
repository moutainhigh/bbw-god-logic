package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 合服每日累充10元
 *
 * @author: huanghb
 * @date: 2022/2/17 14:12
 */
@Service
public class PerDayAccProcessor extends AbstractActivityProcessor {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private RechargeStatisticService rechargeStatisticService;
    /** 初始进度 */
    private Integer INIT_PROGRESS = 0;

    public PerDayAccProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
    }

    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        //获得当前玩家所有活动信息
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
        List<UserActivity> userActivities = activityService.getUserActivities(uid, a.gainId(), ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
        List<RDActivityItem> rdActivityItems = rd.getItems();
        List<CfgActivityEntity> cfgActivityEntities = ActivityTool.getActivitiesByType(ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);

        for (RDActivityItem rdActivityItem : rdActivityItems) {
            UserActivity userActivity = userActivities.stream().filter(tmp -> tmp.getBaseId().equals(rdActivityItem.getId())).findFirst().orElse(null);
            //玩家活动信息为空
            if (null == userActivity) {
                rdActivityItem.setProgress(INIT_PROGRESS);
                continue;
            }
            //是否是否昨天未完成任务
            boolean isYesterdayUnfinishedRecharge = !isToday(userActivity) && userActivity.getStatus() == AwardStatus.UNAWARD.getValue();
            if (isYesterdayUnfinishedRecharge) {
                rdActivityItem.setProgress(INIT_PROGRESS);
                continue;
            }
            //活动进度
            rdActivityItem.setProgress(userActivity.getProgress());
            //活动进度异常修复
            if (userActivity.getProgress() >= cfgActivityEntities.get(0).getNeedValue() && userActivity.getStatus() == AwardStatus.UNAWARD.getValue()) {
                rdActivityItem.setStatus(AwardStatus.ENABLE_AWARD.getValue());
                userActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
                gameUserService.updateItem(userActivity);
            }
        }
        return rd;
    }

    /**
     * 处理合服每日每充值10元服务
     *
     * @param uid
     * @param sid
     * @param progress
     */
    public void handleCombinedServicePerDayAccR10(long uid, int sid, int progress) {
        //活动为空
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
        if (null == a) {
            return;
        }
        //获取活动信息
        List<CfgActivityEntity> cfgActivityEntities = ActivityTool.getActivitiesByType(ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
        List<UserActivity> userActivities = activityService.getUserActivities(uid, a.gainId(), ActivityEnum.COMBINED_SERVICE_PER_ACC_R_10);
        UserActivity userActivity = null;
        //获得用户最后一次活动信息
        if (ListUtil.isNotEmpty(userActivities)) {
            userActivity = userActivities.get(userActivities.size() - 1);
        }
        //最后一次活动信息为空
        if (null == userActivity) {
            userActivity = UserActivity.fromActivity(uid, a.gainId(), 0, cfgActivityEntities.get(0));
            userActivities.add(userActivity);
            activityService.addUserActivity(uid, userActivity);
        }
        //当天充值任务是否完成是否完成
        if (isFinishTodayRechargeTask(userActivity)) {
            return;
        }
        //是否生成新一天的活动信息
        if (userActivity.getStatus() != AwardStatus.UNAWARD.getValue()) {
            userActivity = UserActivity.fromActivity(uid, a.gainId(), 0, cfgActivityEntities.get(userActivities.size()));
            userActivities.add(userActivity);
            activityService.addUserActivity(uid, userActivity);
        }
        //活动时间重置为当天
        if (!isToday(userActivity)) {
            userActivity.setDate(DateUtil.now());
        }
        //今日充值金额
        RechargeStatistic statistic = rechargeStatisticService.fromRedis(uid, DateUtil.getTodayInt());
        Integer todayRecharge = statistic.getToday();
        userActivity.setProgress(todayRecharge);
        //更新状态
        boolean isUpdateStatus = todayRecharge >= cfgActivityEntities.get(userActivities.size() - 1).getNeedValue();
        int status = isUpdateStatus ? AwardStatus.ENABLE_AWARD.getValue() : AwardStatus.UNAWARD.getValue();
        userActivity.setStatus(status);
        //更新拥有项
        gameUserService.updateItem(userActivity);
    }

    /**
     * 是否完成今天的充值任务
     *
     * @param userActivity
     * @return
     */
    public boolean isFinishTodayRechargeTask(UserActivity userActivity) {
        if (!isToday(userActivity)) {
            return false;
        }
        //充值任务是否完成
        if (userActivity.getStatus() == AwardStatus.UNAWARD.getValue()) {
            return false;
        }
        return true;
    }

    /**
     * 是否今天
     *
     * @param userActivity
     * @return
     */
    private boolean isToday(UserActivity userActivity) {
        //活动参与时间
        Date beginTime = userActivity.getDate();
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now);
        //是否今天
        if (daysBetween == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (null != a && null != a.gainEnd()) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
