package com.bbw.god.activity.holiday.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 节日每日每充值10元
 * @date 2020/9/2 15:05
 **/
@Service
public class HolidayPerAccR10Processor extends AbstractActivityProcessor {
    @Autowired
    private RechargeStatisticService statisticService;
    public static final int MAX_PROGRESS = 2000;

    public HolidayPerAccR10Processor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_PER_ACC_R_10
                , ActivityEnum.HOLIDAY_PER_ACC_R_10_51
                , ActivityEnum.HOLIDAY_PER_ACC_R_10_52
        );
    }

    /**
     * 获得奖励
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon joinActivity(Long uid, int sId, int caId, CfgActivityEntity ca, int awardIndex) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ca.getType());
        IActivity a = this.activityService.getActivity(sId, activityEnum);
        // 活动是否过期
        if (a == null) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        RDCommon rd = new RDCommon();
        // 防止并发请求
        GameUser gu = this.gameUserService.getGameUser(uid);
        UserActivity ua = this.activityService.getUserActivity(uid, a.gainId(), ca.getId());
        // 检查活动状态
        AwardStatus status = this.getUAStatus(gu, a, ua, ca);
        this.checkStatusForJoin(status);
        // 发放奖励并处理进度和状态
        RechargeStatistic statistic = statisticService.fromRedis(uid, DateUtil.getTodayInt());
        Integer todayRecharge = statistic.getToday();
        //TODO:充值金额为10时需要修改
        int ableGainTimes = Math.min(todayRecharge, MAX_PROGRESS) / ca.getNeedValue() - ua.getProgress() / ca.getNeedValue();
        List<Award> awards = getAwards(ca);
        for (Award award : awards) {
            award.setNum(award.getNum()*ableGainTimes);
        }
        this.deliver(uid, getWay(), ca.getName(), awards, rd);
        //TODO:充值金额为10时需要修改
        ua.setProgress(ua.getProgress() + ca.getNeedValue() * ableGainTimes);
        if (ua.getProgress() < MAX_PROGRESS) {
            ua.setStatus(AwardStatus.UNAWARD.getValue());
        } else {
            ua.setStatus(AwardStatus.AWARDED.getValue());
        }
        gameUserService.updateItem(ua);
        return rd;
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

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    public List<Award> getAwards(CfgActivityEntity ca) {
        String awards = ca.getAwards();
        if (StrUtil.isBlank(awards)){
            return new ArrayList<>();
        }
        return JSONUtil.fromJsonArray(awards,Award.class);
    }
}
