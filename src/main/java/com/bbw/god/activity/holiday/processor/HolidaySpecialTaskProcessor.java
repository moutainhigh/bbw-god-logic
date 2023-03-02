package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayTaskEntity;
import com.bbw.god.activity.holiday.config.HolidayTaskTool;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 节日特殊任务
 * @date 2020/8/26 15:41
 **/
@Service
public class HolidaySpecialTaskProcessor extends HolidayTaskProcessor {

    public HolidaySpecialTaskProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_SPECIAL_TASK);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    /**
     * 获取节日任务
     *
     * @return
     */
    @Override
    protected List<CfgHolidayTaskEntity> getHolidayTask() {
        return HolidayTaskTool.getSpecialTasks();
    }
}
