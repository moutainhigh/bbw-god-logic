package com.bbw.god.activity.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description 3倍返利
 * @date 2019-11-07 09:20
 **/
@Service
public class MultipleRebateProcessor extends AbstractActivityProcessor {
    @Autowired
    private UserCityService userCityService;

    public MultipleRebateProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.MULTIPLE_REBATE);
    }

    private ActivityEnum getActivityType() {
        return this.activityTypeList.get(0);
    }

    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        AwardStatus awardStatus = super.getUAStatus(gu, a, ua, ca);
        if (ca.isMultipleRebateRechargeNeed()) {
            return awardStatus;
        }
        //获取充值状态
        AwardStatus rechargeStatus = AwardStatus.UNAWARD;
        CfgActivityEntity rca = this.getRechargeActivity();
        UserActivity rechargeActivity = this.activityService.getUserActivity(gu.getId(), a.gainId(), rca.getId());
        if (rechargeActivity != null) {
            //充值档位变低了 此处修复充值金额在  新 旧 之间的数据
            rechargeStatus = AwardStatus.fromValue(rechargeActivity.getStatus());
            int oldStatus=rechargeStatus.getValue();
            if (AwardStatus.UNAWARD.equals(rechargeStatus)){
                rechargeActivity.addProgress(0,rca);
                if (rechargeActivity.getStatus()!=oldStatus){
                    rechargeStatus = AwardStatus.fromValue(rechargeActivity.getStatus());
                    gameUserService.updateItem(rechargeActivity);
                }
            }
            if (AwardStatus.ENABLE_AWARD.equals(rechargeStatus)){
                rechargeActivity.setStatus(AwardStatus.AWARDED.getValue());
                rechargeStatus = AwardStatus.fromValue(rechargeActivity.getStatus());
                gameUserService.updateItem(rechargeActivity);
            }
        }
        //如果充值已达成，且成长计划已达成，则为可领取状态
        if (rechargeStatus == AwardStatus.AWARDED && awardStatus == AwardStatus.ACHIEVED) {
            awardStatus = AwardStatus.ENABLE_AWARD;
        }
        return awardStatus;
    }

    @Override
    protected RDActivityItem toRdActivity(GameUser gu, UserActivity ua, CfgActivityEntity ca, int status) {
        //充值限制的跳过
        if (ca.isMultipleRebateRechargeNeed()) {
            return null;
        }
        RDActivityItem rdActivity = super.toRdActivity(gu, ua, ca, status);
        rdActivity.setSeries(ca.getSeries());
        rdActivity.setTotalProgress(ca.getNeedValue());
        int progress = 0;
        if (ua != null) {
            progress = ua.getProgress();
        }
        rdActivity.setProgress(progress);
        return rdActivity;
    }

    @Override
    protected int getTotalProgress(List<UserActivity> uas) {
        if (ListUtil.isEmpty(uas)) {
            return 0;
        }
        CfgActivityEntity ca = this.getRechargeActivity();
        UserActivity ua = uas.stream().filter(tmp -> tmp.getBaseId().equals(ca.getId())).findFirst().orElse(null);
        if (ua == null) {
            return 0;
        }
        return ua.getProgress();
    }

    /**
     * 处理攻城进度
     *
     * @param guId
     * @param sId
     * @param city
     */
    public void handleAttackProgress(long guId, int sId, CfgCityEntity city) {
        ActivityEnum activityType = this.getActivityType();
        IActivity a = this.activityService.getActivity(sId, activityType);
        if (a == null) {
            return;
        }

        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityType);
        List<UserActivity> uas = this.activityService.getUserActivities(guId, a.gainId(), activityType);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        for (CfgActivityEntity ca : cas) {
            //充值限制的跳过
            if (ca.isMultipleRebateRechargeNeed()) {
                continue;
            }
            // 不是同一个系列的跳过
            if (ca.getSeries() != city.getLevel()) {
                continue;
            }
            // log.info("{}级{}，攻城略地系列{}", city.getLevel(), city.getName(), ca.getName());
            UserActivity ua = null;
            if (isUasNotEmpty) {
                ua = uas.stream().filter(tmp -> tmp.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }
            ua = this.activityService.handleUa(guId, ua, a.gainId(), 1, ca);
            //可领取的标记为已达成
            if (ua.getStatus() == AwardStatus.ENABLE_AWARD.getValue()) {
                ua.setStatus(AwardStatus.ACHIEVED.getValue());
                this.gameUserService.updateItem(ua);
            }
        }
    }

    public void repairAttackProgress(long guId, int sId, int series, int addNum) {
        System.out.println("series=" + series + ",addNum:" + addNum);
        ActivityEnum activityType = getActivityType();
        IActivity a = this.activityService.getActivity(sId, activityType);
        if (a == null) {
            return;
        }

        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityType);
        List<UserActivity> uas = this.activityService.getUserActivities(guId, a.gainId(), activityType);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        for (CfgActivityEntity ca : cas) {
            //充值限制的跳过
            if (ca.isMultipleRebateRechargeNeed()) {
                continue;
            }
            // 不是同一个系列的跳过
            if (ca.getSeries() != series) {
                continue;
            }
            // log.info("{}级{}，攻城略地系列{}", city.getLevel(), city.getName(), ca.getName());
            UserActivity ua = null;
            if (isUasNotEmpty) {
                ua = uas.stream().filter(tmp -> tmp.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }
            ua = this.activityService.handleUa(guId, ua, a.gainId(), addNum, ca);
            //可领取的标记为已达成
            if (ua.getStatus() == AwardStatus.ENABLE_AWARD.getValue()) {
                ua.setStatus(AwardStatus.ACHIEVED.getValue());
                this.gameUserService.updateItem(ua);
            }
        }
    }

    /**
     * 处理充值进度
     *
     * @param guId
     * @param sId
     * @param addedProgress
     */
    public void handleRechargeProgress(long guId, int sId, int addedProgress) {
        ActivityEnum activityType = getActivityType();
        IActivity a = this.activityService.getActivity(sId, activityType);
        // 活动未生效，不做任何处理
        if (a == null) {
            return;
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityType);
        List<UserActivity> uas = this.activityService.getUserActivities(guId, a.gainId(), activityType);
        boolean isUasNotEmpty = ListUtil.isNotEmpty(uas);
        for (CfgActivityEntity ca : cas) {
            //成长计划跳过
            if (!ca.isMultipleRebateRechargeNeed()) {
                continue;
            }
            UserActivity ua = null;
            if (isUasNotEmpty) {
                ua = uas.stream().filter(tmp -> tmp.getBaseId().equals(ca.getId())).findFirst().orElse(null);
            }
            ua = this.activityService.handleUa(guId, ua, a.gainId(), addedProgress, ca);
            //可领取标记为已领取
            if (ua.getStatus() == AwardStatus.ENABLE_AWARD.getValue()) {
                ua.setStatus(AwardStatus.AWARDED.getValue());
                this.gameUserService.updateItem(ua);
                repaireProgress(guId);
            }
        }

    }

    /**
     * 用于更新后修复进度
     *
     * @param uid
     */
    public void repaireProgress(long uid) {
        int sId = this.gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sId, getActivityType());
        if (a == null) {
            return;
        }
        List<UserCity> ucs = userCityService.getUserCities(uid);
        if (ListUtil.isEmpty(ucs)) {
            return;
        }
        Map<Integer, List<UserCity>> ucMaps = ucs.stream().collect(Collectors.groupingBy(tmp -> tmp.gainCity().getLevel()));
        ucMaps.keySet().stream().forEach(tmp -> this.repairAttackProgress(uid, sId, tmp, ucMaps.get(tmp).size()));
//        ucs.forEach(tmp -> {
//            this.handleAttackProgress(uid, sId, tmp.gainCity());
//        });
    }

    private CfgActivityEntity getRechargeActivity() {
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(getActivityType());
        CfgActivityEntity rca = cas.stream().filter(tmp -> tmp.isMultipleRebateRechargeNeed()).findFirst().get();
        return rca;
    }
}
