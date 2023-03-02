package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 累天充值
 * @date 2019-11-07 09:20
 **/
@Service
public class AccumulatedDaysRechargeProcessor extends AbstractActivityProcessor {

    public AccumulatedDaysRechargeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.ACC_R_DAYS_7);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
