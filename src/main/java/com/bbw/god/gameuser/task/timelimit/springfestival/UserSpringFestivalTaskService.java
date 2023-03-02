package com.bbw.god.gameuser.task.timelimit.springfestival;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.timelimit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 春节村庄疑云
 *
 * @author fzj
 * @date 2022/1/5 11:42
 */
@Service
public class UserSpringFestivalTaskService {

    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;

    /**
     * 获取任务列表
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getAllTasks(long uid) {
        return userTimeLimitTaskService.getAllTasks(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK);
    }

    /**
     * 获取任务列表,不包含过期和已领取的活动
     *
     * @param uid
     * @return
     */
    public List<UserTimeLimitTask> getTasks(long uid) {
        return userTimeLimitTaskService.getTasks(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK);
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
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK, task, getRandomSkills(task), null);
        } else {
            instance = UserTimeLimitTask.instance(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK, task);
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
        String cacheKey = "springFestivalTask" + task.getId();
        List<Integer> fromCache = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, List.class);
        if (null != fromCache) {
            return fromCache;
        }
        fromCache = TimeLimitTaskTool.getExtraRandomSkills(TaskGroupEnum.SPRING_FESTIVAL_TASK, task);
        GameDataTimeLimitCacheUtil.cache(cacheKey, fromCache, DateUtil.SECOND_ONE_DAY * 15);
        return fromCache;
    }

}
