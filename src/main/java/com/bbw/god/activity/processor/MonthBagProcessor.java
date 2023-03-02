package com.bbw.god.activity.processor;

import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suhq
 * @description: 月度礼包
 * @date 2019-11-07 09:20
 **/
@Service
public class MonthBagProcessor extends AbstractActivityProcessor {

    public MonthBagProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.MONTH_BAG);
    }

}
