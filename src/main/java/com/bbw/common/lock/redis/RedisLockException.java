package com.bbw.common.lock.redis;

/**
 * Redis分布式锁异常
 *
 * @author: suhq
 * @date: 2022/1/4 11:21 上午
 */
public class RedisLockException extends RuntimeException {
    private static final long serialVersionUID = -1758467826143343345L;

    public RedisLockException(String message) {
        super(message);
    }
}
