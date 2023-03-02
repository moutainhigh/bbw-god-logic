package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayTaskEntity;
import com.bbw.god.activity.holiday.config.HolidayTaskTool;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 节日每日任务
 * @date 2020/8/26 15:41
 **/
@Service
public class HolidayDailyTaskProcessor extends HolidayTaskProcessor {

    public HolidayDailyTaskProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_DAILY_TASK);
    }


    /**
     * 获取节日任务
     *
     * @return
     */
    @Override
    protected List<CfgHolidayTaskEntity> getHolidayTask() {
        return HolidayTaskTool.getDailyTasks();
    }
}
