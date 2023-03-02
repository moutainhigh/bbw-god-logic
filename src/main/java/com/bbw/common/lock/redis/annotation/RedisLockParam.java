package com.bbw.common.lock.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于方法参数，将参数追加到@RedisLock的key上
 *
 * @author: suhq
 * @date: 2022/1/4 10:00 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RedisLockParam {
    /**
     * 无需指定spel的两种情况：<br/>
     * 1.对于简单类型，如数值类型、字符串，自动将这些类型转为字符串，并追加到分布式锁key。<br/>
     * 2.对于实现了RedisLockable的类型，会使用key()的返回值，并追加到分布式锁key。<br/>
     * <br/>
     * 其他情况需指定spel:<br/>
     * 指定了spel的参数,将对指定的参数进行spel解析，并追加到分布式锁key。<br/>
     *
     * @return
     * @see <a href="https://zhuanlan.zhihu.com/p/174786047">玩转Spring中强大的spel表达式！</a>
     */
    String spel() default "";

}
