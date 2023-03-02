package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日任务工具
 * @date 2020/8/26 17:07
 **/
public class HolidayTaskTool {

    public static List<CfgHolidayTaskEntity> getAllTasks() {
        return Cfg.I.get(CfgHolidayTaskEntity.class);
    }

    public static List<CfgHolidayTaskEntity> getDailyTasks() {
        return getAllTasks().stream().filter(t -> t.getType() == 10070).collect(Collectors.toList());
    }

    public static List<CfgHolidayTaskEntity> getSpecialTasks() {
        return getAllTasks().stream().filter(t -> t.getType() == 10080).collect(Collectors.toList());
    }

    public static CfgHolidayTaskEntity getTaskById(Integer id) {
        return Cfg.I.get(id, CfgHolidayTaskEntity.class);
    }
}
