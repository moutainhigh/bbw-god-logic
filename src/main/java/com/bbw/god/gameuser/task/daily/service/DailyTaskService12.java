package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.dice.DiceStatistic;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 任务id%1000=12的任务对应的service
 * @date 2020/11/24 15:45
 **/
@Service
public class DailyTaskService12 extends ResourceDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(21012, 22012, 23012, 24012, 25012);
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
        DiceStatistic statistic = diceStatisticService.fromRedis(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt());
        return statistic.getToday();
    }

    /**
     * 获取当前service对应的资源枚举
     *
     * @return 当前service对应的资源枚举
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.TL;
    }
}
