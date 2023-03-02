package com.bbw.god.gameuser.task.daily.service;

import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 行为每日任务service
 * @date 2020/12/2 18:01
 **/
@Service
public abstract class BehaviorDailyTaskService extends BaseDailyTaskService {

    /**
     * 获取当前service对应的行为枚举
     *
     * @return 当前service对应的行为枚举
     */
    public abstract BehaviorType getMyBehaviorType();

    public boolean isMatch(BehaviorType behaviorType){
        return getMyBehaviorType().equals(behaviorType);
    }
}
