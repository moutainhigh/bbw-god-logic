package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description: 节日登录礼包
 * @date 2020-01-13 09:20
 **/
@Service
public class HolidayBagProcessor extends AbstractActivityProcessor {

    public HolidayBagProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_BAG);
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
}
