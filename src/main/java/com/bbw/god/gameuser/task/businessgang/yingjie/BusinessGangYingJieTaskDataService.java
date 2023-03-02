package com.bbw.god.gameuser.task.businessgang.yingjie;

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
 * 商帮英杰任务redis服务
 *
 * @author: huanghb
 * @date: 2022/7/25 15:07
 */
@Service
public class BusinessGangYingJieTaskDataService {
    @Autowired
    private RedisHashUtil<Integer, UserBusinessGangYingJieTask> businessGangYingJieTRedisUtil;
    /** 缓存天数 */
    private static final Integer CACHE_TIME = 8;

    /**
     * 更新用户任务信息
     *
     * @param uid
     * @param userTask
     */
    public void updateBusinessGangYingJieTaskToCache(long uid, UserBusinessGangYingJieTask userTask) {
        String cacheKey = getBusinessGangYingJieTaskCacheKey(uid);
        businessGangYingJieTRedisUtil.putField(cacheKey, userTask.getTaskId(), userTask, DateUtil.SECOND_ONE_DAY * CACHE_TIME);
    }

    /**
     * 更新用户所有任务信息
     *
     * @param uid
     * @param userTask
     */
    public void updateBusinessGangYingJieTasksToCache(long uid, List<UserBusinessGangYingJieTask> userTask) {
        Map<Integer, UserBusinessGangYingJieTask> userTaskMap = userTask.stream().collect(Collectors.toMap(UserBusinessGangYingJieTask::getTaskId, Function.identity(), (key1, key2) -> key2));
        String cacheKey = getBusinessGangYingJieTaskCacheKey(uid);
        businessGangYingJieTRedisUtil.putAllField(cacheKey, userTaskMap);
        businessGangYingJieTRedisUtil.expire(cacheKey, CACHE_TIME, TimeUnit.DAYS);

    }

    /**
     * 读取任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public UserBusinessGangYingJieTask getBusinessGangYingJieTaskFromCache(long uid, int taskId) {
        String cacheKey = getBusinessGangYingJieTaskCacheKey(uid);
        return businessGangYingJieTRedisUtil.getField(cacheKey, taskId);
    }

    /**
     * 读取任务信息（从缓存）
     *
     * @param uid
     * @return
     */
    public List<UserBusinessGangYingJieTask> getBusinessGangYingJieTasksFromCache(long uid) {
        String cacheKey = getBusinessGangYingJieTaskCacheKey(uid);
        return businessGangYingJieTRedisUtil.get(cacheKey).values().stream().collect(Collectors.toList());
    }

    /**
     * 每日任务缓存key
     *
     * @return
     */
    protected String getBusinessGangYingJieTaskCacheKey(long uid) {
        return "business" + SPLIT + "gang" + SPLIT + "yingjie" + SPLIT + "task" + uid;
    }
}
