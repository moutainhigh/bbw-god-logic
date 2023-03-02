package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 节日签到
 * @date 2020-01-13 09:20
 **/
@Service
public class HolidaySignProcessor extends AbstractActivityProcessor {

    public HolidaySignProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_SIGN
                , ActivityEnum.HOLIDAY_SIGN_51
                , ActivityEnum.HOLIDAY_SIGN_52
        );
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
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
