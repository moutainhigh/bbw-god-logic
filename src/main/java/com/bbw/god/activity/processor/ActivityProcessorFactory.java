package com.bbw.god.activity.processor;

import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动处理工厂类
 */
@Service
public class ActivityProcessorFactory {
    @Autowired
    @Lazy
    private List<IActivityProcessor> activityProcessors;

    /**
     * 根据特定活动的服务实现对象
     *
     * @param activityType
     * @return
     */
    public IActivityProcessor getActivityProcessor(ActivityEnum activityType) {
        return this.activityProcessors.stream().filter(mp -> mp.isMatch(activityType)).findFirst().orElse(null);
    }

}
