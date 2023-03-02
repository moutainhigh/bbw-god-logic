package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description: 节日每日累充
 * @date 2020-01-13 09:20
 **/
@Service
public class HolidayDayAccRProcessor extends AbstractActivityProcessor {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private HolidayPerAccR10Processor holidayPerAccR10Processor;
    /** 充值金额 */
    public final static Integer AWARD_AMOUNT_LIMIT = 2000;

    public HolidayDayAccRProcessor() {
        this.activityTypeList = Arrays.asList(
                ActivityEnum.HOLIDAY_DAY_ACC_R,
                ActivityEnum.HOLIDAY_DAY_ACC_R_1_51,
                ActivityEnum.HOLIDAY_DAY_ACC_R_1_52,
                ActivityEnum.HOLIDAY_DAY_ACC_R2,
                ActivityEnum.HOLIDAY_PER_DAY_ACC_R);
    }

    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        Integer curActivityType = getCurActivityType(uid, activityType);
        RDActivityList rd = (RDActivityList) super.getActivities(uid, curActivityType);
        rd.getItems().sort(Comparator.comparing(RDActivityItem::getTotalProgress));
        int sid = gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(curActivityType);
        //获得每充值实例
        IActivity a = getPerAccRActivity(sid, activityEnum);
        if (a != null) {
            RDActivityItem rdActivity = getRDActivity(uid, a);
            rd.getItems().add(0, rdActivity);
        }
        return rd;
    }

    /**
     * 获得每充值活动
     *
     * @param sid
     * @param activityEnum
     * @return
     */
    private IActivity getPerAccRActivity(int sid, ActivityEnum activityEnum) {
        if (null == activityEnum) {
            return null;
        }
        switch (activityEnum) {
            case HOLIDAY_DAY_ACC_R:
                return this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_PER_ACC_R_10);
            case HOLIDAY_DAY_ACC_R_1_51:
                return this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_PER_ACC_R_10_51);
            case HOLIDAY_DAY_ACC_R_1_52:
                return this.activityService.getActivity(sid, ActivityEnum.HOLIDAY_PER_ACC_R_10_52);
            default:
                return null;
        }
    }

    /**
     * 获取每充10元的rdActivity
     *
     * @param uid
     * @param a
     * @return
     */
    private RDActivityItem getRDActivity(long uid, IActivity a) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(a.gainType());
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivityByType(activityEnum);
        if (null == cfgActivityEntity) {
            return null;
        }
        int activityId = cfgActivityEntity.getId();
        UserActivity userActivity = activityService.getUserActivity(uid, a.gainId(), activityId);
        RDActivityItem rdActivity = new RDActivityItem();
        //TODO:充值金额为10时需要修改
        int progress = null == userActivity ? 0 : userActivity.getProgress() / cfgActivityEntity.getNeedValue();
        int status = null == userActivity ? AwardStatus.UNAWARD.getValue() : userActivity.getStatus();
        rdActivity.setId(activityId);
        // 每10元加一次进度
        // 当前已领取次数
        rdActivity.setProgress(progress);
        // 最高可领取100次
        //TODO:充值金额为10时需要修改
        rdActivity.setTotalProgress(cfgActivityEntity.getNeedValue());
        rdActivity.setStatus(status);
        List<Award> awards = holidayPerAccR10Processor.getAwards(cfgActivityEntity);
        rdActivity.setAwards(awards);
        rdActivity.setTitle("每充值" + cfgActivityEntity.getNeedValue() + "元");
        return rdActivity;
    }


    /**
     * 获取当前开启的活动
     *
     * @param uid
     * @param activityType
     * @return
     */
    private Integer getCurActivityType(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a != null) {
            return activityType;
        }
        List<ActivityEnum> activityEnums = this.activityTypeList.stream().filter(at -> at.getValue() != activityType)
                .collect(Collectors.toList());
        for (ActivityEnum ac : activityEnums) {
            a = this.activityService.getActivity(sid, ac);
            if (a != null) {
                return ac.getValue();
            }
        }
        throw new ExceptionForClientTip("activity.not.exist");
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (null != a && null != a.gainEnd()) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
