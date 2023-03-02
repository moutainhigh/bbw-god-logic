package com.bbw.god.gameuser.statistic.behavior.task;

import com.bbw.common.DateUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.timelimit.cunz.CunZTaskEnum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 村庄任务统计service
 *
 * @author fzj
 * @date 2021/8/11 17:28
 */
@Service
public class CunZTaskStatisticService extends BehaviorStatisticService {
    /**
     * 获取当前行为类型
     *
     * @return 当前行为类型
     */
    @Override
    public BehaviorType getMyBehaviorType() {
        return BehaviorType.FINISH_CUNZ_TASK;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof CunZTaskStatistic)) {
            throw new CoderException("参数类型错误！");
        }
        CunZTaskStatistic cunZTaskStatistic = (CunZTaskStatistic) statistic;
        Integer date = cunZTaskStatistic.getDate();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, cunZTaskStatistic.getToday());
        map.put(TOTAL, cunZTaskStatistic.getTotal());
        //任务类型
        Map<String, Integer> typeAccomplishedNums = cunZTaskStatistic.getTypeAccomplishedNums();
        if (null != typeAccomplishedNums) {
            for (String typeField : typeAccomplishedNums.keySet()) {
                map.put(typeField, typeAccomplishedNums.get(typeField));
            }
        }
        //任务难度
        Map<String, Integer> difficultyAccomplishedNums = cunZTaskStatistic.getDifficultyAccomplishedNums();
        if (null != difficultyAccomplishedNums) {
            for (String difficultyField : difficultyAccomplishedNums.keySet()) {
                map.put(difficultyField, difficultyAccomplishedNums.get(difficultyField));
            }
        }
        //npc任务
        Map<String, Integer> npcAccomplishedNums = cunZTaskStatistic.getNpcAccomplishedNums();
        if (null != npcAccomplishedNums) {
            for (String npcField : npcAccomplishedNums.keySet()) {
                map.put(npcField, npcAccomplishedNums.get(npcField));
            }
        }
        String key = getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);

    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return 统计对象
     */
    @Override
    public CunZTaskStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        String redisKey = getKey(uid, typeEnum);
        Map<String, Integer> redisMap = redisHashUtil.get(redisKey);
        return new CunZTaskStatistic(date, redisMap);
    }

    @Override
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) new CunZTaskStatistic(date, redisMap);
    }

    /**
     * 该任务是否参与统计
     *
     * @param taskId
     * @return
     */
    public boolean isToStatistic(Integer taskId) {
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.CUN_ZHUANG_TASK, taskId);
        TaskDifficulty difficulty = TaskDifficulty.fromValue(taskEntity.getDifficulty());
        //非最后的史诗级任务不计入统计
        if (difficulty == TaskDifficulty.SUPER_LEVEL && taskEntity.getSeq() != 3) {
            return false;
        }
        return true;
    }

    /**
     * 执行统计
     *
     * @param uid
     * @param taskId
     */
    public void doStatistic(long uid, int taskId, boolean FirstAchieved) {
        //全量统计
        increment(uid, DateUtil.getTodayInt(), 1);
        //判断任务类别
        String key = getKey(uid, StatisticTypeEnum.NONE);
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(TaskGroupEnum.CUN_ZHUANG_TASK, taskId);
        //特定类型任务
        TaskTypeEnum taskType = TaskTypeEnum.fromValue(taskEntity.getType());
        redisHashUtil.increment(key, taskType.getName(), 1);
        //判断任务难度
        TaskDifficulty difficulty = TaskDifficulty.fromValue(taskEntity.getDifficulty());
        redisHashUtil.increment(key, difficulty.getName(), 1);
        //派遣任务(不重复)的NPC
        CunZTaskEnum npc = CunZTaskEnum.fromValue(taskEntity.getSeqGroup());
        if (null != npc && FirstAchieved) {
            redisHashUtil.increment(key, npc.getName(), 1);
        }
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {

    }
}
