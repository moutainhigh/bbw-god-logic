package com.bbw.common.lock;

import java.util.function.Function;

/**
 * @author suchaobin
 * @description lock锁接口
 * @date 2020/8/18 9:57
 **/
public interface LockUtil {
    /**
     * 执行锁操作
     *
     * @param key
     * @param function
     * @return
     */
    Object doSafe(String key, Function<Object, Object> function);
}
