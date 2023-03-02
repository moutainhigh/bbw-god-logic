package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskDifficulty;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 村庄任务信息
 *
 * @author: suhq
 * @date: 2021/8/6 4:45 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCunzTasksInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 已达成的任务ID */
    private List<Integer> achievedIds;
    /** 完成难度统计 */
    private Map<String, Integer> difficultyFinishedNums;

    public static UserCunzTasksInfo instance(long uid) {
        UserCunzTasksInfo instance = new UserCunzTasksInfo();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        return instance;
    }

    /**
     * 添加已达成过的任务ID
     *
     * @param taskId
     */
    public void addAchieved(int taskId) {
        if (null == achievedIds) {
            achievedIds = new ArrayList<>();
        }
        if (achievedIds.contains(taskId)) {
            return;
        }
        achievedIds.add(taskId);
    }

    /**
     * 获取某个难度的完成数量
     *
     * @param difficulty
     * @return
     */
    public int gainFinishedNum(TaskDifficulty difficulty) {
        return difficultyFinishedNums.getOrDefault(difficulty.getValue(), 0);
    }

    /**
     * 添加完成次数
     *
     * @param taskId
     */
    public void addDifficultyFinishedNum(int taskId) {
        if (null == difficultyFinishedNums) {
            difficultyFinishedNums = new HashMap<>();
        }
        CfgTaskEntity task = TaskTool.getTaskEntity(TaskGroupEnum.CUN_ZHUANG_TASK, taskId);
        String difficultyKey = task.getDifficulty().toString();
        Integer finishedNum = difficultyFinishedNums.getOrDefault(difficultyKey, 0);
        finishedNum++;
        difficultyFinishedNums.put(difficultyKey, finishedNum);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_CUNZ_TASKS_INFO;
    }

}
