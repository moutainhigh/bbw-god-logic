package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @description 任务id%1000=19的任务对应的service
 * @date 2020/11/25 16:01
 **/
@Service
public class DailyTaskService19 extends ResourceDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(22019, 23019);
    }

    /**
     * 获取当前任务进度(用于判断任务是否完成)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    @Override
    public int doGetProgress(long uid, int level, int taskId, UserTaskInfo info) {
        CopperStatistic statistic = copperStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        Map<WayEnum, Long> todayMap = statistic.getTodayMap();
        Long val = todayMap.get(WayEnum.SALARY_COPPER);
        return val == null ? 0 : 1;
    }

    /**
     * 获取当前service对应的资源枚举
     *
     * @return 当前service对应的资源枚举
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.TQ;
    }
}
