package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.god.gameuser.task.TaskDifficulty;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试村庄任务
 *
 * @author: suhq
 * @date: 2021/8/20 8:56 上午
 */
public class CunZTaskToolTest {

    /**
     * 测试村庄任务随机难度概率
     */
    @Test
    public void getRanomDifficulty() {
        Map<String, Integer> difficultyFinishedNums = new HashMap<>();
        difficultyFinishedNums.put(TaskDifficulty.FIRST_LEVEL.getValue() + "", 5);
        difficultyFinishedNums.put(TaskDifficulty.MIDDLE_LEVEL.getValue() + "", 5);
        difficultyFinishedNums.put(TaskDifficulty.HIGH_LEVEL.getValue() + "", 0);
        difficultyFinishedNums.put(TaskDifficulty.SUPER_LEVEL.getValue() + "", 0);
        Map<TaskDifficulty, Integer> results = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            TaskDifficulty ranomDifficulty = CunZTaskTool.getRanomDifficulty(difficultyFinishedNums);
            Integer num = results.getOrDefault(ranomDifficulty, 0);
            num++;
            results.put(ranomDifficulty, num);
        }
        for (TaskDifficulty difficulty : results.keySet()) {
            System.out.println(difficulty + ":" + results.get(difficulty));
        }
    }
}