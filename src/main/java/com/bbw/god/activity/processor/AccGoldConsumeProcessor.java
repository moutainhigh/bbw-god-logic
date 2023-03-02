package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 累计元宝消耗处理器
 * @date 2021/1/18 18:01
 **/
@Service
public class AccGoldConsumeProcessor extends AbstractActivityProcessor{
    public AccGoldConsumeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.ACC_GOLD_CONSUME);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        return NO_TIME;
    }
}
