package com.bbw.god.gameuser.task.daily.service;

import com.bbw.exception.CoderException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 每日任务工厂类
 * @date 2020/11/24 11:39
 **/
@Service
public class DailyTaskServiceFactory {
    @Autowired
    @Lazy
    private List<BaseDailyTaskService> dailyTaskServices;
    @Autowired
    @Lazy
    private List<ResourceDailyTaskService> resourceDailyTaskServices;
    @Autowired
    @Lazy
    private List<BehaviorDailyTaskService> behaviorDailyTaskServices;

    /**
     * 通过任务id获取对应service
     *
     * @param taskId 任务id
     * @return 任务id的对应service
     */
    public BaseDailyTaskService getById(int taskId) {
        for (BaseDailyTaskService service : dailyTaskServices) {
            if (service.getMyTaskIds().contains(taskId)) {
                return service;
            }
        }
        throw new CoderException(String.format("程序员没有编写任务id=%s的service", taskId));
    }

    /**
     * 根据资源类型获取对应service集合
     *
     * @param awardEnum 资源类型枚举
     * @return 资源类型对应service集合
     */
    public List<ResourceDailyTaskService> getByAwardEnum(AwardEnum awardEnum) {
        return resourceDailyTaskServices.stream().filter(tmp ->
                tmp.getMyAwardEnum().equals(awardEnum)).collect(Collectors.toList());
    }

    /**
     * 根据行为类型获取对应service集合
     *
     * @param behaviorType 行为类型枚举
     * @return 行为类型对应service集合
     */
    public List<BehaviorDailyTaskService> getByBehaviorType(BehaviorType behaviorType) {
        return behaviorDailyTaskServices.stream().filter(tmp ->
                tmp.isMatch(behaviorType)).collect(Collectors.toList());
    }
}
