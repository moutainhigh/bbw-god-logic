package com.bbw.db.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * redis-list访问接口
 *
 * @author suhq
 * @date 2021-02-25 15:41
 **/
@Component
public class RedisListUtil<T> extends RedisBase {
    private ListOperations<String, T> getLop(String key) {
        return (ListOperations<String, T>) getTemplate(key).opsForList();
    }

    /**
     * 完整的list
     *
     * @param key
     */
    public List<T> get(String key) {
        return getLop(key).range(key, 0, this.getSize(key));
    }

    /**
     * 输出list
     *
     * @param key List的key
     * @param s   开始下标
     * @param e   结束的下标
     * @return
     */
    public List<T> get(String key, long s, long e) {
        return getLop(key).range(key, s, e);
    }

    /**
     * 获取list集合中元素的个数
     *
     * @param key
     * @return
     */
    public long getSize(String key) {
        Optional<Long> num = Optional.ofNullable(getLop(key).size(key));
        return num.orElse(0L);
    }

    public long leftPush(String key, T obj) {
        Optional<Long> num = Optional.ofNullable(getLop(key).leftPush(key, obj));
        return num.orElse(0L);
    }

    public long leftPushAll(String key, Collection<T> objs) {
        Optional<Long> num = Optional.ofNullable(getLop(key).leftPushAll(key, objs));
        return num.orElse(0L);
    }

    public T leftPop(String key) {
        return getLop(key).leftPop(key);
    }

    public long leftPushIfPresent(String key, T obj) {
        Long lValue = getLop(key).leftPushIfPresent(key, obj);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 移除list中某值
     *
     * @param key
     * @param ts
     * @return 返回移除数量
     */
    public long remove(String key, Collection<T> ts) {
        long r = 0;
        for (T t : ts) {
            r += this.remove(key, t);
        }
        return r;
    }

    /**
     * 移除list中某值,可能存在多个对象
     *
     * @param key
     * @param t
     * @return 返回移除数量
     */
    public long remove(String key, T t) {
        Long lValue = getLop(key).remove(key, 0, t);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 添加一个集合
     *
     * @param key
     * @param objs
     */
    public void rightPush(String key, Collection<T> objs) {
        getLop(key).rightPushAll(key, objs);
    }

    /**
     * 向list中增加值
     *
     * @param key
     * @param obj
     * @return 返回在list中的下标
     */
    public long rightPush(String key, T obj) {
        Long lValue = getLop(key).rightPush(key, obj);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    public T rightPop(String key) {
        return getLop(key).rightPop(key);
    }

    public long rightPushIfPresent(String key, T obj) {
        Long lValue = getLop(key).rightPushIfPresent(key, obj);
        return Optional.ofNullable(lValue).orElse(0L);
    }
}
