package com.bbw.god.gameuser.task.activitytask;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.tmp.AbstractTmpDataRedisService;
import com.bbw.god.cache.tmp.TmpDataType;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 活动每日任务redis服务
 *
 * @author: huanghb
 * @date: 2022/11/14 10:02
 */
@Service
public class ActivityDailyTaskDataService extends AbstractTmpDataRedisService<UserActivityDailyTask, Long> {
    /** 缓存天数 */
    private static final Integer CACHE_TIME = 30;

    /**
     * 获取业务数据类型
     *
     * @return
     */
    @Override
    protected Class<UserActivityDailyTask> getDataClazz() {
        return UserActivityDailyTask.class;
    }

    /**
     * 获取数据归属
     *
     * @param data
     * @return
     */
    @Override
    protected Long getDataBelong(UserActivityDailyTask data) {
        return data.getUid();
    }

    /**
     * 获取循环数据的循环标识
     *
     * @param data
     * @return
     */
    @Override
    protected String getDataLoop(UserActivityDailyTask data) {
        return data.getTaskType() + SPLIT + DateUtil.toDateInt(DateUtil.fromDateLong(data.getGenerateTime()));
    }

    /**
     * 获取Redis key
     *
     * @param belong
     * @param dataType
     * @param loop
     * @return
     */
    @Override
    protected String getRedisKey(Long belong, TmpDataType dataType, String... loop) {
        String keySuffix = StringUtils.join(loop, SPLIT);
        return "usr" + SPLIT + belong + SPLIT + dataType.getRedisKey() + SPLIT + keySuffix;
    }

    /**
     * 获取Redis hash field
     *
     * @param data
     * @return
     */
    @Override
    protected Long getField(UserActivityDailyTask data) {
        return Long.valueOf(data.getTaskId());
    }

    /**
     * 获取过期时间（ms）
     *
     * @param data
     * @return
     */
    @Override
    protected long getExpiredMillis(UserActivityDailyTask data) {
        return DateUtil.SECOND_ONE_DAY * CACHE_TIME * 1000L;
    }

}
