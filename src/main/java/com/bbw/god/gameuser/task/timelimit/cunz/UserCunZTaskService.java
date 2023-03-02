package com.bbw.god.gameuser.task.timelimit.cunz;

import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskDifficulty;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 村庄任务服务
 *
 * @author: suhq
 * @date: 2021/8/6 4:55 下午
 */
@Service
public class UserCunZTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;

    /**
     * 获取任务列表，不包含过期和已领取的活动
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getTasks(long uid) {
        return userTimeLimitTaskService.getTasks(uid, TaskGroupEnum.CUN_ZHUANG_TASK);
    }

    /**
     * 获取当前任务数
     *
     * @param uid
     * @return
     */
    public int getTaskCount(long uid) {
        return getTasks(uid).size();
    }

    /**
     * 添加新村村庄任务
     *
     * @param uid
     * @return
     */
    public UserTimeLimitTask addNewTask(long uid) {
        UserCunzTasksInfo cunzTasksInfo = gameUserService.getSingleItem(uid, UserCunzTasksInfo.class);
        TaskDifficulty difficulty = TaskDifficulty.FIRST_LEVEL;
        List<Integer> achieveIds = new ArrayList<>();
        if (null != cunzTasksInfo && null != cunzTasksInfo.getDifficultyFinishedNums()) {
            difficulty = CunZTaskTool.getRanomDifficulty(cunzTasksInfo.getDifficultyFinishedNums());
        }
        if (null != cunzTasksInfo && null != cunzTasksInfo.getAchievedIds()) {
            achieveIds = cunzTasksInfo.getAchievedIds();
        }
        CfgTaskEntity cunZRandomTask = CunZTaskTool.getCunZRandomTask(difficulty, achieveIds);
        UserTimeLimitTask instance = makeUserTaskInstance(uid, cunZRandomTask);
        gameUserService.addItem(uid, instance);
        return instance;
    }

    /**
     * 构建用户任务实例
     *
     * @param uid
     * @param cunZTask
     * @return
     */
    public UserTimeLimitTask makeUserTaskInstance(long uid, CfgTaskEntity cunZTask) {
        UserTimeLimitTask instance = null;
        if (TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() == cunZTask.getType()) {
            List<Integer> extraRandomSkills = TimeLimitTaskTool.getExtraRandomSkills(TaskGroupEnum.CUN_ZHUANG_TASK, cunZTask);
            List<Award> extraRandomAward = TimeLimitTaskTool.getExtraRandomAward(TaskGroupEnum.CUN_ZHUANG_TASK, cunZTask);
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.CUN_ZHUANG_TASK, cunZTask, extraRandomSkills, extraRandomAward);
        } else {
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.CUN_ZHUANG_TASK, cunZTask);
        }
        return instance;
    }

}
