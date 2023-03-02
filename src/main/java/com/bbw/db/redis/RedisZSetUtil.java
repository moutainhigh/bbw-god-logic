package com.bbw.db.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * redis-zset访问接口
 *
 * @author suhq
 * @date 2021-02-25 15:27
 **/
@Slf4j
@Component
public class RedisZSetUtil<T> extends RedisBase {
    private ZSetOperations<String, T> getZop(String key) {
        return (ZSetOperations<String, T>) getTemplate(key).opsForZSet();
    }

    /**
     * 添加有序集合ZSET 默认按照score升序排列，存储格式K(1)==V(n)，V(1)=S(1)
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean add(String key, T value, double score) {
        return getZop(key).add(key, value, score);
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Boolean add(String key, double[] score, T[] value) {
        if (score.length != value.length) {
            return false;
        }
        for (int i = 0; i < score.length; i++) {
            if (this.add(key, value[i], score[i]) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param value
     * @return
     */
    public Long add(String key, TreeSet<TypedTuple<T>> value) {
        return getZop(key).add(key, value);
    }

    /**
     * 键为K的集合，sMin<=score<=sMax的元素个数
     *
     * @param key
     * @param sMin
     * @param sMax
     * @return
     */
    public long getCountSize(String key, double sMin, double sMax) {
        Long lValue = getZop(key).count(key, sMin, sMax);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 获取整个有序集合ZSET，正序(从小到大)
     *
     * @param key
     */
    public Set<T> range(String key) {
        return range(key, 0, this.size(key));
    }

    /**
     * 获取有序集合ZSET 键为K的集合，索引start<=index<=end的元素子集，正序
     *
     * @param key
     * @param startIndex 开始位置
     * @param endIndex   结束位置
     */
    public Set<T> range(String key, long startIndex, long endIndex) {
        return getZop(key).range(key, startIndex, endIndex);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序(从大到小)
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Nullable
    public Set<T> rangeByScore(String key, double start, double end) {
        return getZop(key).rangeByScore(key, start, end);
    }

    /**
     * 键为K的集合 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @return
     */
    public Set<TypedTuple<T>> rangeWithScores(String key) {
        return rangeWithScores(key, 0, this.size(key));
    }

    /**
     * 键为K的集合，索引start<=index<=end的元素子集 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<T>> rangeWithScores(String key, long start, long end) {
        return getZop(key).rangeWithScores(key, start, end);
    }

    /**
     * 获取整个有序集合ZSET，倒序
     *
     * @param key
     */
    public Set<T> reverseRange(String key) {
        return reverseRange(key, 0, size(key));
    }

    /**
     * 获取有序集合ZSET 键为K的集合，索引startIndex<=index<=endIndex的元素子集，倒序
     *
     * @param key
     * @param startIndex 开始位置
     * @param endIndex   结束位置
     */
    public Set<T> reverseRange(String key, long startIndex, long endIndex) {
        return getZop(key).reverseRange(key, startIndex, endIndex);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序 -从大到小
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<T> reverseRangeByScore(String key, double start, double end) {
        return getZop(key).reverseRangeByScore(key, start, end);
    }

    /**
     * 键为K的集合 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @return
     */
    public Set<TypedTuple<T>> reverseRangeWithScores(String key) {
        return reverseRangeWithScores(key, 0, size(key));
    }

    /**
     * 键为K的集合，索引startIndex<=index<=endIndex的元素子集 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @param startIndex
     * @param endIndex
     * @return
     */
    public Set<TypedTuple<T>> reverseRangeWithScores(String key, long startIndex, long endIndex) {
        return getZop(key).reverseRangeWithScores(key, startIndex, endIndex);
    }

    /**
     * 获取Zset 键为K的集合元素个数
     *
     * @param key
     * @return
     */
    public long size(String key) {
        Long lValue = getZop(key).size(key);
        return Optional.ofNullable(lValue).orElse(0L);
    }

    /**
     * 获取键为key的集合，value为T的元素分数
     *
     * @param key
     * @param value
     * @return
     */
    public double score(String key, T value) {
        Double lValue = getZop(key).score(key, value);
        return Optional.ofNullable(lValue).orElse(0.0);
    }

    /**
     * 元素分数增加，delta是增量
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public double incrementScore(String key, T value, double delta) {
        Double lValue = getZop(key).incrementScore(key, value, delta);
        return Optional.ofNullable(lValue).orElse(0.0);
    }

    /**
     * 移除key的ZSet
     *
     * @param key
     * @return
     */
    public void remove(String key) {
        removeRange(key, 0L, this.size(key));
    }

    /**
     * 移除key对应Zset中为value的条目
     *
     * @param key
     * @param values
     * @return
     */
    public Long remove(String key, T... values) {
        return getZop(key).remove(key, values);
    }

    /**
     * 删除，键为K的集合，索引startIndex<=index<=endIndex的元素子集
     *
     * @param key
     * @param startIndex
     * @param endIndex
     * @return
     */
    public void removeRange(String key, Long startIndex, Long endIndex) {
        getZop(key).removeRange(key, startIndex, endIndex);
    }

    /**
     * 通过分数删除ZSet中的值
     *
     * @param key
     * @param s
     * @param e
     */
    public void removeRangeByScore(String key, double s, double e) {
        getZop(key).removeRangeByScore(key, s, e);
    }

    /**
     * 并集 将unionKey1、unionKey2对应的集合合并到storeKey中 如果分数相同的值，都会保留 原来key2的值会被覆盖
     * !!!unionKey1、unionKey2、storeKey必须是统一数据源
     *
     * @param unionKey1
     * @param unionKey1
     * @param storeKey
     */
    public void unionAndStore(String unionKey1, String unionKey2, String storeKey) {
        getZop(unionKey1).unionAndStore(unionKey1, unionKey2, storeKey);
    }

    /**
     * 获得值的排行(score按从小到大)
     *
     * @param key
     * @param value
     * @return
     */
    public Long rank(String key, T value) {
        return getZop(key).rank(key, value);
    }

    /**
     * 获得值得排名（score按从大到小）
     *
     * @param key
     * @param value
     * @return
     */
    public Long reverseRank(String key, T value) {
        return getZop(key).reverseRank(key, value);
    }

    /**
     * 获取排行的值
     *
     * @param key
     * @param rank
     * @return
     */
    public T rankObject(String key, int rank) {
        Set<T> sets = getZop(key).range(key, rank - 1, rank - 1);
        if (null != sets) {
            return sets.iterator().next();
        }
        return null;
    }
}