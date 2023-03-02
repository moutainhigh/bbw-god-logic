package com.bbw.god.gameuser.task.biggodplan;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 大仙计划任务redis服务
 *
 * @author: huanghb
 * @date: 2022/2/17 14:14
 */
@Service
public class BigGodPlanTaskDataService {
    @Autowired
    private RedisHashUtil<Integer, UserBigGodPlanTask> bigGodPlanTaskRedisUtil;
    /** 缓存天数 */
    private static final Integer CACHE_TIME = 7;

    /**
     * 更新用户大仙计划任务信息
     *
     * @param uid
     * @param userBigGodPlanTask
     */
    public void updateBigGodPlanTaskToCache(long uid, UserBigGodPlanTask userBigGodPlanTask) {
        String cacheKey = getBigGodPlanTaskCacheKey(uid);
        bigGodPlanTaskRedisUtil.putField(cacheKey, userBigGodPlanTask.getTaskId(), userBigGodPlanTask, DateUtil.SECOND_ONE_DAY * CACHE_TIME);
    }

    /**
     * 更新用户大仙计划所有任务信息
     *
     * @param uid
     * @param userBigGodPlanTasks
     */
    public void updateBigGodPlanTasksToCache(long uid, List<UserBigGodPlanTask> userBigGodPlanTasks) {
        Map<Integer, UserBigGodPlanTask> userBigGodPlanTaskMap = userBigGodPlanTasks.stream().collect(Collectors.toMap(UserBigGodPlanTask::getTaskId, Function.identity(), (key1, key2) -> key2));
        String cacheKey = getBigGodPlanTaskCacheKey(uid);
        bigGodPlanTaskRedisUtil.putAllField(cacheKey, userBigGodPlanTaskMap);
    }

    /**
     * 读取大仙计划任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserBigGodPlanTask getBigGodPlanTaskFromCache(long uid, int taskId) {
        String cacheKey = getBigGodPlanTaskCacheKey(uid);
        return bigGodPlanTaskRedisUtil.getField(cacheKey, taskId);
    }

    /**
     * 读取大仙计划所有任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public List<UserBigGodPlanTask> getBigGodPlanTasksFromCache(long uid) {
        String cacheKey = getBigGodPlanTaskCacheKey(uid);
        return bigGodPlanTaskRedisUtil.get(cacheKey).values().stream().collect(Collectors.toList());
    }

    /**
     * 大仙计划缓存key
     *
     * @return
     */
    protected String getBigGodPlanTaskCacheKey(long uid) {
        return "big" + SPLIT + "god" + SPLIT + "plan" + SPLIT + "task" + uid;
    }
}
