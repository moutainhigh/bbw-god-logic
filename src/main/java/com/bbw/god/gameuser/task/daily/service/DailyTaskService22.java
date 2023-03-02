package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.fight.FightStatistic;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @description 任务id%1000=22的任务对应的service
 * @date 2020/11/25 16:01
 **/
@Service
public class DailyTaskService22 extends BehaviorDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(22022, 23022);
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
        FightStatistic statistic = fightStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        Map<FightTypeEnum, Integer> todayFightMap = statistic.getTodayFightMap();
        Integer val = todayFightMap.get(FightTypeEnum.SXDH);
        return val == null ? 0 : val;
    }

    /**
     * 获取当前service对应的行为枚举
     *
     * @return 当前service对应的行为枚举
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FIGHT;
    }
}
