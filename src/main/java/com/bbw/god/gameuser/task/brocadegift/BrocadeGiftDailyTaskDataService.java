package com.bbw.god.gameuser.task.brocadegift;

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
 * 锦礼每日任务redis服务
 *
 * @author: huanghb
 * @date: 2022/2/17 14:14
 */
@Service
public class BrocadeGiftDailyTaskDataService {
    @Autowired
    private RedisHashUtil<Integer, UserBrocadeGiftDailyTask> brocadeGiftDailyGiftTaskRedisUtil;
    /** 缓存天数 */
    protected static final Integer CACHE_TIME = 8;

    /**
     * 更新用户锦礼每日任务信息
     *
     * @param uid
     * @param userBrocadeGiftDailyTask
     */
    public void updateBrocadeGiftDailyTaskToCache(long uid, UserBrocadeGiftDailyTask userBrocadeGiftDailyTask) {
        String cacheKey = getBrocadeGiftDailyTaskCacheKey(uid);
        brocadeGiftDailyGiftTaskRedisUtil.putField(cacheKey, userBrocadeGiftDailyTask.getTaskId(), userBrocadeGiftDailyTask, DateUtil.SECOND_ONE_DAY * CACHE_TIME);
    }

    /**
     * 更新用户锦礼每日任务信息
     *
     * @param uid
     * @param userBrocadeGiftDailyTasks
     */
    public void updateBrocadeGiftDailyTasksToCache(long uid, List<UserBrocadeGiftDailyTask> userBrocadeGiftDailyTasks) {
        Map<Integer, UserBrocadeGiftDailyTask> userBrocadeGiftDailyTaskMap = userBrocadeGiftDailyTasks.stream().collect(Collectors.toMap(UserBrocadeGiftDailyTask::getTaskId, Function.identity(), (key1, key2) -> key2));
        String cacheKey = getBrocadeGiftDailyTaskCacheKey(uid);
        brocadeGiftDailyGiftTaskRedisUtil.putAllField(cacheKey, userBrocadeGiftDailyTaskMap);
        brocadeGiftDailyGiftTaskRedisUtil.expire(cacheKey, CACHE_TIME, TimeUnit.DAYS);

    }

    /**
     * 读取锦礼每日任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserBrocadeGiftDailyTask getBrocadeGiftDailyTaskFromCache(long uid, int taskId) {
        String cacheKey = getBrocadeGiftDailyTaskCacheKey(uid);
        return brocadeGiftDailyGiftTaskRedisUtil.getField(cacheKey, taskId);
    }

    /**
     * 读取锦礼每日任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public List<UserBrocadeGiftDailyTask> getBrocadeGiftDailyTasksFromCache(long uid) {
        String cacheKey = getBrocadeGiftDailyTaskCacheKey(uid);
        return brocadeGiftDailyGiftTaskRedisUtil.get(cacheKey).values().stream().collect(Collectors.toList());
    }

    /**
     * 锦礼每日任务缓存key
     *
     * @return
     */
    protected String getBrocadeGiftDailyTaskCacheKey(long uid) {
        return "brocade" + SPLIT + "gift" + SPLIT + "daily" + SPLIT + "task" + uid;
    }
}
