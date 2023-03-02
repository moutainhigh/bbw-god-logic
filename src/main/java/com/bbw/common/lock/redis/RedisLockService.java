package com.bbw.common.lock.redis;

import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.db.redis.RedisValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Redis加锁、释放锁服务
 *
 * @author: suhq
 * @date: 2022/1/4 10:05 上午
 */
@Slf4j
@Component
public class RedisLockService {
    @Autowired
    private RedisValueUtil<String> redisValueUtil;

    /**
     * 加分布式锁，如果加锁成功，则将locker标记为加锁者。
     * <br/>
     * 为了避免@RedisLock嵌套使用出现死锁，同一个线程内的相关业务需支持可重入锁。
     *
     * @param lockKey
     * @param locker  锁获得者，用于放置误释放（只能释放自己获得的锁）
     * @return true:获取锁(或者已获取锁)，可执行后续逻辑
     */
    public boolean lock(String lockKey, String locker, RedisLock redisLock) {
        //是否已获取锁
        boolean hasLock = locker.equals(RedisLockContextHolder.getLocker());
        if (hasLock) {
            return true;
        }

        // 加锁
        long start = System.currentTimeMillis();
        while (true) {
            try {
                // 尝试获取锁
                boolean success = add(lockKey, locker, redisLock.liveTime());
                if (success) {
                    RedisLockContextHolder.setLocker(locker);
                    return true;
                }
                // 获取超时，跳出死循环
                if (System.currentTimeMillis() - start >= redisLock.waitTimeOut()) {
                    break;
                }
                // 没有获得锁，等待后重新尝试获取锁
                Thread.sleep(redisLock.tryInterval());
            } catch (Exception e) {
                log.error(locker + "获取锁" + lockKey + "失败：" + e.getMessage(), e);
                try {
                    del(lockKey, locker);
                    return false;
                } finally {
                    RedisLockContextHolder.clear();
                }
            }
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public void unlock(String lockKey, String unlocker) {
        boolean isAbleToUnlock = RedisLockContextHolder.getLocker().equals(unlocker);
        if (!isAbleToUnlock) {
            return;
        }
        try {
            RedisLockContextHolder.clear();
        } finally {
            del(lockKey, unlocker);
        }
    }


    /**
     * redis设置锁
     *
     * @param lockKey
     * @return true:成功在Redis中添加lockKey
     */
    private boolean add(String lockKey, String locker, long liveTime) {
        return redisValueUtil.setNX(lockKey, locker, liveTime);
    }

    /**
     * redis删除锁
     *
     * @param lockKey
     * @param deleter
     * @return true:成功从Redis中删除lockKey
     */
    private boolean del(String lockKey, String deleter) {
        String locker = redisValueUtil.get(lockKey);
        if (null == locker || !locker.equals(deleter)) {
            return false;
        }
        return redisValueUtil.delete(lockKey);
    }

}
