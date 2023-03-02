package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.copper.CopperStatistic;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 任务id%1000=16的任务对应的service
 * @date 2020/11/24 16:00
 **/
@Service
public class DailyTaskService16 extends ResourceDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(21016, 22016, 23016, 24016, 25016);
    }

    /**
     * 获取当前任务所需值
     *
     * @param level  玩家等级
     * @param taskId 任务id
     * @return 当前任务所需值
     */
    @Override
    public int getMyNeedValue(int level, int taskId) {
        if (!getMyTaskIds().contains(taskId)) {
            throw new CoderException(String.format("当前service不支持id=%s的任务", taskId));
        }
        return 20000 * level;
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
        return statistic.getTodayNum().intValue();
    }

    /**
     * 获取当前任务进度(用于展示给客户端)
     *
     * @param uid    玩家id
     * @param level  玩家等级
     * @param taskId 任务id
     * @param info   任务对象信息
     * @return 当前任务进度
     */
    @Override
    public int getMyProgressForShow(long uid, int level, int taskId, UserTaskInfo info) {
        int myProgress = getMyProgress(uid, level, taskId, info);
        return myProgress / 10000;
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
