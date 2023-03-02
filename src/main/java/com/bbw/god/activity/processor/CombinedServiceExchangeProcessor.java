package com.bbw.god.activity.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.CombinedServiceMallExchangeProcessor;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

/**
 * 合服-兑换
 *
 * @author: huanghb
 * @date: 2022/2/17 14:12
 */
@Service
public class CombinedServiceExchangeProcessor extends AbstractActivityProcessor {
    @Autowired
    private CombinedServiceMallExchangeProcessor combinedServiceExchangeMallProcessor;
    /** 功能开放天数 */
    private static final int OPEN_DAYS = 5;

    public CombinedServiceExchangeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.COMBINED_SERVICE_EXCHANGE);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        int sid = gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity iActivity = this.activityService.getActivity(sid, activityEnum);
        if (iActivity == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDMallList malls = combinedServiceExchangeMallProcessor.getGoods(uid);
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, iActivity));
        Date beginTime = iActivity.gainBegin();
        rd.upDateIsTodayOpened(beginTime, OPEN_DAYS);
        return rd;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
