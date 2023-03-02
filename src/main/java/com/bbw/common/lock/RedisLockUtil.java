package com.bbw.common.lock;

import com.bbw.db.redis.RedisValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author suchaobin
 * @description redis分布锁
 * @date 2020/7/2 15:32
 **/
@Component
@Slf4j
public class RedisLockUtil implements LockUtil {
    @Autowired
    private RedisValueUtil<String> redisValueUtil;

    /**
     * 尝试获取锁的时间
     */
    private static final Long WAIT_TIME = 5 * 1000L;
    /**
     * 自动释放锁的时间
     */
    private static final Long AUTO_RELEASE_TIME = 10 * 1000L;

    /**
     * 加锁
     *
     * @param key          redis锁的key
     * @param milliseconds 毫秒
     * @return
     */
    private Boolean lock(String key, long milliseconds) {
        String value = "1";
        return redisValueUtil.setNX(key, value, milliseconds);
    }

    /**
     * 释放锁
     *
     * @param key redis锁的key
     */
    private void releaseLock(String key) {
        redisValueUtil.delete(key);
    }

    /**
     * 加锁执行业务操作，结束后释放锁.获取锁失败后尝试再获取的时间间隔为20ms
     *
     * @param lockKey
     * @param function
     * @return
     */
    @Override
    public Object doSafe(String lockKey, Function<Object, Object> function) {
        return doSafe(lockKey, 20, function);
    }

    /**
     * 加锁执行
     *
     * @param lockKey
     * @param tryInterval 获取锁失败后尝试再获取的时间间隔(ms)
     * @param function
     * @return
     */
    public Object doSafe(String lockKey, int tryInterval, Function<Object, Object> function) {
        long start = System.currentTimeMillis();
        while (true) {
            // 尝试获取锁
            if (lock(lockKey, AUTO_RELEASE_TIME)) {
                try {
                    return function.apply(lockKey);
                } finally {
                    // 释放锁
                    releaseLock(lockKey);
                }
            } else {
                // 获取超时，跳出死循环
                if (System.currentTimeMillis() - start >= WAIT_TIME) {
                    break;
                }
                // 没获取到锁，说明别人获取到了
                try {
                    // 等多久后重新尝试获取锁
                    Thread.sleep(tryInterval);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
