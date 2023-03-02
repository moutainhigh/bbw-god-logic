package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 活动总览处理器
 * @date 2020/9/1 14:29
 **/
@Service
public class HolidayOverviewProcessor extends AbstractActivityProcessor {
    public HolidayOverviewProcessor() {
        this.activityTypeList = Arrays.asList(
                ActivityEnum.HOLIDAY_OVERVIEW,
                ActivityEnum.HOLIDAY_OVERVIEW_51,
                ActivityEnum.HOLIDAY_OVERVIEW_52,
                ActivityEnum.ACTIVITY_OVERVIEW_MODEL_1,
                ActivityEnum.ACTIVITY_OVERVIEW_MODEL_2,
                ActivityEnum.COMBINED_SERVICE_ACTIVITY_OVERVIEW_MODEL,
                ActivityEnum.WORLD_CUP_ACTIVITY_OVERVIEW);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
