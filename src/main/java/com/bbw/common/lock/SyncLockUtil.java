package com.bbw.common.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author suchaobin
 * @description 本地锁工具类
 * @date 2020/8/18 9:17
 **/
@Component
public class SyncLockUtil implements LockUtil {
    /**
     * key是玩家id，value是对应的byte数组，根据byte数组来执行锁操作
     */
    private static final ConcurrentHashMap<Long, byte[]> LOCK_MAP = new ConcurrentHashMap<>();

    /**
     * 获取玩家对应的byte数组
     *
     * @param uid
     * @return
     */
    private static byte[] getUserByteArr(long uid) {
        return LOCK_MAP.computeIfAbsent(uid, k -> new byte[0]);
    }

    /**
     * 添加到LOCK_MAP，已经有的不会重复put
     *
     * @param uid
     */
    public void putToLockMap(long uid) {
        getUserByteArr(uid);
    }

    /**
     * 执行锁操作
     *
     * @param uid
     * @param function
     * @return
     */
    @Override
    public Object doSafe(String uid, Function<Object, Object> function) {
        byte[] bytes = getUserByteArr(Long.parseLong(uid));
        synchronized (bytes) {
            return function.apply(uid);
        }
    }

}
