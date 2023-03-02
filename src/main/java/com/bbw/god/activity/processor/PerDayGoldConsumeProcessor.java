package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 每日消费
 * @date 2019-11-07 09:20
 **/
@Service
public class PerDayGoldConsumeProcessor extends AbstractActivityProcessor {

    public PerDayGoldConsumeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.PER_DAY_GOLD_CONSUME);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        return DateUtil.getTimeToNextDay();
    }
}
