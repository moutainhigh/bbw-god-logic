package com.bbw.god.gameuser.task.halloweenRestaurant;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.HolidayHalloweenRestaurantProcessor;
import com.bbw.god.gameuser.task.AbstractTaskProcessor;
import com.bbw.god.gameuser.task.RDTaskList;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 万圣餐厅每日任务
 *
 * @author: huanghb
 * @date: 2022/10/12 16:59
 */
@Component
public class HalloweenRestaurantDailyTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private HalloweenRestaurantDailyTaskService halloweenRestaurantDailyTaskService;
    @Autowired
    private HolidayHalloweenRestaurantProcessor holidayHalloweenRestaurantProcessor;

    public HalloweenRestaurantDailyTaskProcessor() {
        this.taskTypes = Arrays.asList(TaskTypeEnum.HALLOWEEN_RESTAURANT_LIMIT_TASK);
    }

    /**
     * 获得任务列表
     *
     * @param uid
     * @return
     */
    @Override
    public RDTaskList getTasks(long uid, Integer days) {
        if (!holidayHalloweenRestaurantProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return halloweenRestaurantDailyTaskService.getTasks(uid, days);
    }

    /**
     * 获取任务奖励
     *
     * @param uid
     * @param id
     * @param awardIndex
     * @return
     */
    @Override
    public RDCommon gainTaskAward(long uid, int id, String awardIndex) {
        if (!holidayHalloweenRestaurantProcessor.isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return halloweenRestaurantDailyTaskService.gainTaskAward(uid, id);
    }
}
