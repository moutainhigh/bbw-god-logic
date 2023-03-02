package com.bbw.common.lock.redis;

/**
 * 对于以自定义类作为RedisLockParam的，可以自定义key。
 * <br/>
 * 该接口仅适用于被@RedisLockParam注解的参数类
 *
 * @author: suhq
 * @date: 2022/1/4 11:43 上午
 */
public interface RedisLockable {
    /**
     * 自定义key
     *
     * @return
     */
    String key();
}
