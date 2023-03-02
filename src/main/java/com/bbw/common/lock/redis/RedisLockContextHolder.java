package com.bbw.common.lock.redis;

/**
 * 基于ThreadLocal实现可重入锁
 *
 * @author: suhq
 * @date: 2022/1/4 10:34 上午
 */
public class RedisLockContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static synchronized void setLocker(String locker) {
        CONTEXT_HOLDER.set(locker);
    }

    public static String getLocker() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
