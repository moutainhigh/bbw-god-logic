package com.bbw.god.gameuser.task.daily.service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import com.bbw.god.gameuser.task.UserTaskInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 任务id%1000=14的任务对应的service
 * @date 2020/11/24 15:49
 **/
@Service
public class DailyTaskService14 extends ResourceDailyTaskService {
    /**
     * 获取当前service对应的任务id集合
     *
     * @return 当前service对应的任务id集合
     */
    @Override
    public List<Integer> getMyTaskIds() {
        return Arrays.asList(21014, 22014, 23014, 24014, 25014);
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
        List<CfgTreasureEntity> mapTreasures = TreasureTool.getAllTreasures().stream().filter(tmp ->
                tmp.getType().equals(TreasureType.MAP_TREASURE.getValue())).collect(Collectors.toList());
        TreasureStatistic statistic = treasureStatisticService.fromRedis(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt());
        for (CfgTreasureEntity treasure : mapTreasures) {
            int consumeNum = statistic.getTodayNum(TreasureTool.getTreasureById(treasure.getId()));
            if (consumeNum > 0) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 获取当前service对应的资源枚举
     *
     * @return 当前service对应的资源枚举
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.FB;
    }
}
