package com.bbw.common.lock.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于Redis的分布锁标记。
 * <br/>
 * 被标记的方法将加上分布式锁。
 * 加锁方法尽量只处理真正需要加锁的必要业务。
 *
 * @author: suhq
 * @date: 2022/1/4 9:45 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisLock {

    /**
     * redis key 的值
     *
     * @return
     */
    String key() default "";

    /**
     * 抛出异常时，抛出的信息
     *
     * @return 错误信息
     */
    String error() default "加锁异常，请重试";

    /**
     * 锁的有效期(ms)。默认10,000ms
     *
     * @return 锁的超时时间(ms)
     */
    long liveTime() default 10000L;

    /**
     * 获取锁的超时时间(ms)。5,000ms
     *
     * @return 锁的超时时间(ms)
     */
    long waitTimeOut() default 5000L;

    /**
     * 获取锁失败后尝试再获取的时间间隔(ms)。默认20ms
     *
     * @return 获取锁失败后尝试再获取的时间间隔(ms)
     */
    long tryInterval() default 20L;
}