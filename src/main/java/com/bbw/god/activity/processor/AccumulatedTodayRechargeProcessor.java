package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.bbw.god.game.award.AwardStatus.ENABLE_AWARD;

/**
 * @author suhq
 * @description: 今日累充
 * @date 2019-11-07 09:20
 **/
@Service
public class AccumulatedTodayRechargeProcessor extends AbstractActivityProcessor {
    @Autowired
    private DailyShakeService dailyShakeService;

    @Override
    public WayEnum getWay() {
        return super.getWay();
    }

    public AccumulatedTodayRechargeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.TODAY_ACC_R, ActivityEnum.TODAY_ACC_R_2, ActivityEnum.TODAY_ACC_R_3);
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        //获取活动信息
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        List<RDActivityItem> items = rd.getItems();
        //获取福利信息
        CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
        if (null == welfare) {
            return rd;
        }
        if (!welfare.getMallIds().contains(0)) {
            return rd;
        }
        rd.setDailyShakeWelfareId(welfare.getId());
        for (RDActivityItem rdActivityItem : items) {
            //任务已完成
            if (rdActivityItem.getStatus() != AwardStatus.UNAWARD.getValue()) {
                continue;
            }
            //获取福利加成
            int welfareAdd = welfare.getWelfareAdds().get(0);
            //福利条件未达成
            if (rd.getTotalProgress() < welfareAdd) {
                continue;
            }
            //是否加入福利
            if (rd.getTotalProgress() + welfareAdd < rdActivityItem.getTotalProgress()) {
                continue;
            }
            rdActivityItem.setStatus(ENABLE_AWARD.getValue());


        }
        return rd;
    }

    /**
     * 获得活动状态
     *
     * @param gu
     * @param a
     * @param ua
     * @param ca
     * @return
     */
    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        if (ua != null) {
            if (ua.getStatus() != AwardStatus.UNAWARD.getValue()) {
                return AwardStatus.fromValue(ua.getStatus());
            }
            CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(gu.getId());
            if (null == welfare) {
                return AwardStatus.fromValue(ua.getStatus());
            }
            if (!welfare.getMallIds().contains(0)) {
                return AwardStatus.fromValue(ua.getStatus());
            }
            //获取福利加成
            int welfareAdd = welfare.getWelfareAdds().get(0);
            if (ua.getProgress() < welfareAdd) {
                return AwardStatus.fromValue(ua.getStatus());
            }
            if (ua.getProgress() + welfareAdd < ca.getNeedValue()) {
                return AwardStatus.fromValue(ua.getStatus());
            }
            return AwardStatus.ENABLE_AWARD;
        }
        // 如果有领取条件，且领取条件为0，且没有领取记录，则返回可领取
        if (ca.getNeedValue() != null && ca.getNeedValue() == 0) {
            return ENABLE_AWARD;
        }
        return AwardStatus.UNAWARD;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        return DateUtil.getTimeToNextDay();
    }
}
