package com.bbw.db.redis;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * redis-set访问接口
 *
 * @author suhq
 * @date 2021-02-25 15:38
 **/
@Component
public class RedisSetUtil<T> extends RedisBase {
    private SetOperations getSop(String key) {
        return getTemplate(key).opsForSet();
    }

    /**
     * 向set中加入对象
     *
     * @param key  对象key
     * @param objs 值
     */
    @SuppressWarnings("unchecked")
    public Long add(String key, T... objs) {
        return getSop(key).add(key, objs);
    }

    /**
     * 判断set中是否存在这个值
     *
     * @param key 对象key
     */
    public Boolean isMember(String key, T obj) {
        Boolean isMember = getSop(key).isMember(key, obj);
        return isMember == null ? false : isMember;
    }

    /**
     * 获得整个set
     *
     * @param key 对象key
     */
    public Set<T> members(String key) {
        return getSop(key).members(key);
    }

    public List<T> randomMembers(String key, long limit) {
        return getSop(key).randomMembers(key, limit);
    }

    /**
     * 获得set 交集
     *
     * @param key
     * @param set
     * @return
     */
    public Set<T> getIntersect(String key, Set<String> set) {
        return getSop(key).intersect(key, set);
    }

    /**
     * 获得set 交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<T> getIntersect(String key, String otherKey) {
        return getSop(key).intersect(key, otherKey);
    }

    /**
     * 获取set的对象数
     *
     * @param key 对象key
     */
    public Long size(String key) {
        return getSop(key).size(key);
    }

    /**
     * 获得set 并集
     *
     * @param key
     * @param set
     * @return
     */
    public Set<T> union(String key, Set<String> set) {
        return getSop(key).union(key, set);
    }

    /**
     * 获得set 并集。!!!key和otherKey必须在同一数据源
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<T> union(String key, String otherKey) {
        return getSop(key).union(key, otherKey);
    }

    /**
     * 获取差值。!!!key和otherKey必须在同一数据源
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<T> difference(String key, String otherKey) {
        return getSop(key).difference(key, otherKey);
    }

    /**
     * 获取差值。!!!key和otherKey必须在同一数据源
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<T> difference(String key, Collection<String> otherKeys) {
        return getSop(key).difference(key, otherKeys);
    }

    /**
     * 移除set中的某个值
     *
     * @param key 对象key
     * @param obj 值
     */
    public long remove(String key, T obj) {
        if (null == obj) {
            return -1;
        }
        Long lValue = getSop(key).remove(key, obj);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 移除set中的某些值
     *
     * @param key
     * @param values
     * @return
     */
    public long remove(String key, T... values) {
        if (null == values) {
            return -1;
        }
        Long lValue = getSop(key).remove(key, values);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 移除set中的某些值
     *
     * @param key
     * @param values
     * @return
     */
    public long remove(String key, Collection<String> values) {
        if (null == values) {
            return -1;
        }
        Long lValue = getSop(key).remove(key, values.toArray());
        return Optional.ofNullable(lValue).orElse(0L);
    }
}
