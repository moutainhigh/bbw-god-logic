package com.bbw.god.gameuser.task.timelimit.qingming;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.timelimit.TimeLimitTaskTool;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 玩家清明任务服务
 *
 * @author fzj
 * @date 2022/3/28 11:00
 */
@Service
public class UserQingMingTaskService {
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;

    /**
     * 获取任务列表
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getAllTasks(long uid) {
        return userTimeLimitTaskService.getAllTasks(uid, TaskGroupEnum.QING_MING_TASK);
    }

    /**
     * 获取指定任务
     * @param uid
     * @param taskId
     * @return
     */
    public UserTimeLimitTask getTask(long uid, int taskId){
        return getTasks(uid).stream().filter(t -> t.getBaseId() == taskId).findFirst().orElse(null);
    }
    /**
     * 获取任务列表,不包含过期和已领取的活动
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getTasks(long uid) {
        return userTimeLimitTaskService.getTasks(uid, TaskGroupEnum.QING_MING_TASK);
    }

    /**
     * 构建用户任务实例
     *
     * @param uid
     * @param task
     * @return
     */
    public UserTimeLimitTask makeUserTaskInstance(long uid, CfgTaskEntity task) {
        UserTimeLimitTask instance;
        if (TaskTypeEnum.TIME_LIMIT_DISPATCH_TASK.getValue() == task.getType()) {
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.QING_MING_TASK, task, getRandomSkills(task), null);
        } else {
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.QING_MING_TASK, task);
        }
        return instance;
    }


    /**
     * 获取随机技能
     *
     * @param task
     * @return
     */
    private List<Integer> getRandomSkills(CfgTaskEntity task) {
        String cacheKey = "qingMingTask" + task.getId();
        List<Integer> fromCache = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, List.class);
        if (null != fromCache) {
            return fromCache;
        }
        fromCache = TimeLimitTaskTool.getExtraRandomSkills(TaskGroupEnum.QING_MING_TASK, task);
        GameDataTimeLimitCacheUtil.cache(cacheKey, fromCache, DateUtil.SECOND_ONE_DAY * 12);
        return fromCache;
    }
}
