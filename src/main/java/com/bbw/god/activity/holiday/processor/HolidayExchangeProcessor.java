package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidayExchangeMallProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 节日兑换
 * @date 2020-01-13 09:20
 **/
@Service
public class HolidayExchangeProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayExchangeMallProcessor holidayExchangeMallProcessor;

    public HolidayExchangeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_EXCHANGE);
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
    public RDActivityList getActivities(long uid, int activityType) {
        int sid = gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDMallList malls = holidayExchangeMallProcessor.getGoods(uid);
        RDActivityList rd = new RDActivityList();
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, a));
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
