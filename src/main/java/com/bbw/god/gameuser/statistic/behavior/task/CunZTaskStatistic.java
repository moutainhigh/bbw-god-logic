package com.bbw.god.gameuser.statistic.behavior.task;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.task.TaskDifficulty;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.timelimit.cunz.CunZTaskEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 村庄任务统计
 *
 * @author fzj
 * @date 2021/8/11 17:27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CunZTaskStatistic extends BehaviorStatistic {

    /** 特定类型任务达成统计 TaskTypeEnum:达成数量 */
    private Map<String, Integer> typeAccomplishedNums;
    /** 难度任务达成统计 TaskDifficulty:达成数量 */
    private Map<String, Integer> difficultyAccomplishedNums;
    /** NPC任务达成统计 */
    private Map<String, Integer> npcAccomplishedNums;

    public CunZTaskStatistic() {
        super(BehaviorType.FINISH_CUNZ_TASK);
    }

    public CunZTaskStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.FINISH_CUNZ_TASK);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        setToday(today);
        setTotal(total);

        //类型统计
        Map<String, Integer> typeMap = new HashMap<>();
        List<String> typeFields = Arrays.asList(TaskTypeEnum.TIME_LIMIT_NORMAL.getName(), TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getName(), TaskTypeEnum.TIME_LIMIT_FIGHT_TASK.getName());
        for (String typeField : typeFields) {
            Integer num = redisMap.get(typeField);
            num = num == null ? 0 : num;
            typeMap.put(typeField, num);
        }
        setTypeAccomplishedNums(typeMap);

        //难度任务统计
        Map<String, Integer> difficultyMap = new HashMap<>();
        for (TaskDifficulty taskDifficulty : TaskDifficulty.values()) {
            String difficultyField = taskDifficulty.getName();
            Integer num = redisMap.get(difficultyField);
            num = num == null ? 0 : num;
            difficultyMap.put(difficultyField, num);
        }
        setDifficultyAccomplishedNums(difficultyMap);

        //npc任务统计
        Map<String, Integer> npcMap = new HashMap<>();
        for (CunZTaskEnum cunZTask : CunZTaskEnum.values()) {
            String npcField = cunZTask.getName();
            Integer num = redisMap.get(npcField);
            num = num == null ? 0 : num;
            npcMap.put(npcField, num);
        }
        setNpcAccomplishedNums(npcMap);
    }


}
