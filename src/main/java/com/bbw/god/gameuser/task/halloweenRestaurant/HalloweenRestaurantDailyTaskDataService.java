package com.bbw.god.gameuser.task.halloweenRestaurant;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 万圣餐厅每日任务redis服务
 *
 * @author: huanghb
 * @date: 2022/10/14 9:18
 */
@Service
public class HalloweenRestaurantDailyTaskDataService {
    @Autowired
    private RedisHashUtil<Integer, UserHalloweenRestaurantDailyTask> taskRedisUtil;
    /** 缓存天数 */
    private static final Integer CACHE_TIME = 7;

    /**
     * 更新用户每日任务信息
     *
     * @param uid
     * @param task
     */
    public void updateDailyTaskToCache(long uid, UserHalloweenRestaurantDailyTask task) {
        String cacheKey = getDailyTaskCacheKey(uid);
        taskRedisUtil.putField(cacheKey, task.getTaskId(), task, DateUtil.SECOND_ONE_DAY * CACHE_TIME);
    }

    /**
     * 更新用户每日任务信息
     *
     * @param uid
     * @param tasks
     */
    public void updateDailyTasksToCache(long uid, List<UserHalloweenRestaurantDailyTask> tasks) {
        Map<Integer, UserHalloweenRestaurantDailyTask> taskMap = tasks.stream().collect(Collectors.toMap(UserHalloweenRestaurantDailyTask::getTaskId, Function.identity(), (key1, key2) -> key2));
        String cacheKey = getDailyTaskCacheKey(uid);
        taskRedisUtil.putAllField(cacheKey, taskMap);
        taskRedisUtil.expire(cacheKey, CACHE_TIME, TimeUnit.DAYS);
    }

    /**
     * 读取每日任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserHalloweenRestaurantDailyTask getDailyTaskFromCache(long uid, int taskId) {
        String cacheKey = getDailyTaskCacheKey(uid);
        return taskRedisUtil.getField(cacheKey, taskId);
    }

    /**
     * 读取锦礼每日任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public List<UserHalloweenRestaurantDailyTask> getDailyTasksFromCache(long uid) {
        String cacheKey = getDailyTaskCacheKey(uid);
        return taskRedisUtil.get(cacheKey).values().stream().collect(Collectors.toList());
    }

    /**
     * 每日任务缓存key
     *
     * @return
     */
    protected String getDailyTaskCacheKey(long uid) {
        return "usr" + SPLIT + uid + SPLIT + "halloweenCandy" + SPLIT + "dailyTask";
    }
}
