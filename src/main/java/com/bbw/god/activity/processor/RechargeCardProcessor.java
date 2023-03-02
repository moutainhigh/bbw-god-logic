package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

/**
 * @author suhq
 * @description: 充值卡
 * @date 2019-11-07 09:20
 **/
@Service
public class RechargeCardProcessor extends AbstractActivityProcessor {

    @Autowired
    private UserPayInfoService userPayInfoService;

    public RechargeCardProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.RECHARGE_CARD);
    }

    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        // 月卡
        if (ca.isYueKa()) {
            return this.getYkStatus(gu);
        }
        // 季卡
        if (ca.isJiKa()) {
            return this.getJkStatus(gu);
        }

        return AwardStatus.UNAWARD;
    }

    @Override
    protected RDActivityItem toRdActivity(GameUser gu, UserActivity ua, CfgActivityEntity ca, int status) {
        RDActivityItem rdActivity = super.toRdActivity(gu, ua, ca, status);
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
        if (ca.isYueKa()) {
            rdActivity.setRemainTime(this.getYkRemainDays(userPayInfo));
        } else if (ca.isJiKa()) {
            rdActivity.setRemainTime(this.getJkRemainDays(userPayInfo));
        }
        return rdActivity;
    }


    @Override
    protected void updateAwardedStatus(GameUser gu, long aId, int awardIndex, CfgActivityEntity ca) {
        if (ca.isYueKa()) {
            /** 月卡无用户活动记录 */
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            userPayInfo.setYkAwardTime(DateUtil.now());
            gameUserService.updateItem(userPayInfo);
        } else if (ca.isJiKa()) {
            /** 季卡无用户活动记录 */
            UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
            userPayInfo.setJkAwardTime(DateUtil.now());
            gameUserService.updateItem(userPayInfo);
        }
    }

    /**
     * 返回月卡剩余多少天,包括今天
     *
     * @return
     */
    public int getYkRemainDays(UserPayInfo userPayInfo) {
        Date ykEndDate = userPayInfo.getYkEndTime();
        if (ykEndDate != null && DateUtil.millisecondsInterval(ykEndDate, new Date()) > 0) {
            return Math.max(0, DateUtil.getDaysBetween(DateUtil.now(), ykEndDate) + 1);
        }
        return 0;
    }

    /**
     * 获得季卡剩余时间
     *
     * @param userPayInfo
     * @return
     */
    public int getJkRemainDays(UserPayInfo userPayInfo) {
        Date jkEndDate = userPayInfo.getJkEndTime();
        if (jkEndDate != null && DateUtil.millisecondsInterval(jkEndDate, new Date()) > 0) {
            return Math.max(0, DateUtil.getDaysBetween(DateUtil.now(), jkEndDate) + 1);
        }
        return 0;
    }

    /**
     * 获得月卡状态
     *
     * @param gu
     * @return
     */
    private AwardStatus getYkStatus(GameUser gu) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
        return this.getRechargeCardStatus(userPayInfo.getYkEndTime(), userPayInfo.getYkAwardTime());
    }

    /**
     * 获得季卡状态
     *
     * @param gu
     * @return
     */
    private AwardStatus getJkStatus(GameUser gu) {
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(gu.getId());
        return this.getRechargeCardStatus(userPayInfo.getJkEndTime(), userPayInfo.getJkAwardTime());
    }

    /**
     * 获得充值卡状态
     *
     * @param endDate
     * @param awardDate
     * @return
     */
    private AwardStatus getRechargeCardStatus(Date endDate, Date awardDate) {
        Date now = DateUtil.now();
        if (endDate == null) {
            return AwardStatus.UNAWARD;
        }
        if (awardDate == null) {
            return AwardStatus.ENABLE_AWARD;
        }

        if (DateUtil.getDaysBetween(now, endDate) >= 0) {
            // 有生效的月卡
            if (DateUtil.getDaysBetween(awardDate, now) == 0) {
                // 已领取
                return AwardStatus.AWARDED;
            }
            // 可领取
            return AwardStatus.ENABLE_AWARD;
        }
        return AwardStatus.UNAWARD;
    }
}
