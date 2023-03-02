package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.miaoy.MiaoYStatistic;
import com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram.HexagramStatistic;
import com.bbw.god.gameuser.statistic.behavior.miaoy.hexagram.HexagramStatisticService;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 任务id%1000=310的任务对应的service
 * @date 2020/11/25 15:32
 **/
@Service
public class DailyTaskService310 extends BehaviorDailyTaskService {
    @Autowired
    private HexagramStatisticService hexagramStatisticService;
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(21310, 22310, 23310, 24310, 25310);
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
        MiaoYStatistic statistic = miaoYStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        HexagramStatistic hexagramStatistic=hexagramStatisticService.fromRedis(uid,StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        return statistic.getToday()+hexagramStatistic.getToday();
    }

    /**
     * 获取当前service对应的行为枚举
     *
     * @return 当前service对应的行为枚举
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.WWM_DRAW;
    }

    @Override
    public boolean isMatch(BehaviorType behaviorType) {
        return super.isMatch(behaviorType) || BehaviorType.WWM_HEXAGRAM.equals(behaviorType);
    }
}