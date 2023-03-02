package com.bbw.god.activity.processor;

import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 周度礼包
 * @date 2019-11-07 09:20
 **/
@Service
public class WeekBagProcessor extends AbstractActivityProcessor {

    public WeekBagProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.WEEK_BAG);
    }

}
