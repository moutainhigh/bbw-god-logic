package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidayFlowerToGodMallProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 花赋予神
 *
 * @author: huanghb
 * @date: 2022/3/7 9:44
 */
@Service
public class HolidayFlowerToGodProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayFlowerToGodMallProcessor holidayFlowerToGodMallProcessor;

    public HolidayFlowerToGodProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.FLOWER_TO_GOD);
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
        RDMallList malls = holidayFlowerToGodMallProcessor.getGoods(uid);
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
