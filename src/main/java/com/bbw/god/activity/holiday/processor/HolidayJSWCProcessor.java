package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.processor.ActivityProcessorFactory;
import com.bbw.god.activity.processor.IActivityProcessor;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 金鼠旺财
 * @date 2020/9/1 15:31
 **/
@Service
public class HolidayJSWCProcessor extends AbstractActivityProcessor {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityProcessorFactory activityProcessorFactory;

    private static final List<ActivityEnum> TYPES = Arrays.asList(ActivityEnum.HOLIDAY_EXCHANGE
            , ActivityEnum.HOLIDAY_DAILY_TASK
            , ActivityEnum.HOLIDAY_SPECIAL_TASK
            , ActivityEnum.HOLIDAY_SIGN
            , ActivityEnum.HOLIDAY_SIGN_51
            , ActivityEnum.HOLIDAY_SIGN_52
    );

    public HolidayJSWCProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_JSWC);
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        int num = 0;
        for (ActivityEnum activityEnum : TYPES) {
            IActivity iActivity = activityService.getActivity(gu.getServerId(), activityEnum);
            if (null == iActivity) {
                continue;
            }
            IActivityProcessor processor = activityProcessorFactory.getActivityProcessor(activityEnum);
            num += processor.getAbleAwardedNum(gu, iActivity);
        }
        return num;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
