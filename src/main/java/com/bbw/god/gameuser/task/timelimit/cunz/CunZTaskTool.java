package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskDifficulty;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 村庄任务工具类
 *
 * @author: suhq
 * @date: 2021/8/5 5:52 下午
 */
@Slf4j
public class CunZTaskTool {
    /** 用于生成村庄任务的最大任务池 */
    private static List<CfgTaskEntity> CUNZ_MAX_TASK_POOL;
    private static List<TaskDifficulty> DIFFICULTIES = Arrays.asList(TaskDifficulty.FIRST_LEVEL, TaskDifficulty.MIDDLE_LEVEL, TaskDifficulty.HIGH_LEVEL, TaskDifficulty.SUPER_LEVEL);

    static {
        CUNZ_MAX_TASK_POOL = CloneUtil.cloneList(TaskTool.getTaskConfig(TaskGroupEnum.CUN_ZHUANG_TASK).getTasks());
        CUNZ_MAX_TASK_POOL = CUNZ_MAX_TASK_POOL.stream()
                .filter(tmp -> tmp.getSeqGroup() == 0 || (tmp.getSeqGroup() > 0 && tmp.getSeq() == 1))
                .collect(Collectors.toList());
    }

    /**
     * 获取随机难度
     *
     * @param difficultyFinishedNums
     * @return
     */
    public static TaskDifficulty getRanomDifficulty(Map<String, Integer> difficultyFinishedNums) {
        List<Integer> probs;
        if (difficultyFinishedNums.getOrDefault(TaskDifficulty.HIGH_LEVEL.getValue() + "", 0) >= 5) {
            probs = Arrays.asList(35, 30, 20, 15);
        } else if (difficultyFinishedNums.getOrDefault(TaskDifficulty.MIDDLE_LEVEL.getValue() + "", 0) >= 5) {
            probs = Arrays.asList(45, 35, 20, 0);
        } else if (difficultyFinishedNums.getOrDefault(TaskDifficulty.FIRST_LEVEL.getValue() + "", 0) >= 5) {
            probs = Arrays.asList(60, 40, 0, 0);
        } else {
            probs = Arrays.asList(100, 30, 0, 0);
        }
        int index = PowerRandom.getIndexByProbs(probs, 100);
        return DIFFICULTIES.get(index);
    }

    /**
     * 获取随机任务
     *
     * @param difficulty
     * @param achievedIds
     * @return
     */
    public static CfgTaskEntity getCunZRandomTask(TaskDifficulty difficulty, List<Integer> achievedIds) {
        List<CfgTaskEntity> cunZTaskPool = getCunZTaskPool(achievedIds);
        List<CfgTaskEntity> poolFiltWithDifficulty = cunZTaskPool.stream()
                .filter(tmp -> tmp.getDifficulty() == difficulty.getValue())
                .collect(Collectors.toList());
        //对应难度定时器不存在则获取低一个级别的任务
        if (ListUtil.isEmpty(poolFiltWithDifficulty)) {
            TaskDifficulty taskDifficulty = TaskDifficulty.fromValue(difficulty.getValue() - 10);
            poolFiltWithDifficulty = cunZTaskPool.stream()
                    .filter(tmp -> tmp.getDifficulty() == taskDifficulty.getValue())
                    .collect(Collectors.toList());
            log.error("任务生成出错，当前任务等级为{},当前完成的任务id为{}", difficulty.getName(), StringUtils.join(achievedIds, ","));
        }
        return PowerRandom.getRandomFromList(poolFiltWithDifficulty);
    }

    /**
     * 获取玩家当前可用于生成村庄任务的任务池(最大难度限制)
     *
     * @param achievedIds
     * @return
     */
    private static List<CfgTaskEntity> getCunZTaskPool(List<Integer> achievedIds) {
        Map<Integer, Optional<CfgTaskEntity>> seqGroupMapMaxDifficulty = CUNZ_MAX_TASK_POOL.stream()
                .filter(tmp -> achievedIds.contains(tmp.getId()))
                .collect(Collectors.groupingBy(CfgTaskEntity::getSeqGroup, Collectors.maxBy(Comparator.comparingInt(CfgTaskEntity::getDifficulty))));
        List<CfgTaskEntity> pool = new ArrayList<>();
        for (CfgTaskEntity task : CUNZ_MAX_TASK_POOL) {
            Integer difficulty = 0;
            Optional<CfgTaskEntity> op = seqGroupMapMaxDifficulty.get(task.getSeqGroup());
            if (null != op && op.isPresent()) {
                difficulty = op.get().getDifficulty();
            }
            difficulty += 10;
            if (task.getDifficulty() > difficulty) {
                continue;
            }
            pool.add(task);
        }
        return pool;
    }
}
