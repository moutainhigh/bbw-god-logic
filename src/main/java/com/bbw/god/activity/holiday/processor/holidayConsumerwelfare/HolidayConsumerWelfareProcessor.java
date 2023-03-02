package com.bbw.god.activity.holiday.processor.holidayConsumerwelfare;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 消费福利实现类
 *
 * @author: huanghb
 * @date: 2022/12/27 15:18
 */
@Slf4j
@Service
public class HolidayConsumerWelfareProcessor extends AbstractActivityProcessor {
    
    public HolidayConsumerWelfareProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CONSUMPTION_WELFARE);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (null != a.gainEnd()) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
