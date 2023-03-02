package com.bbw.db.redis;

import com.bbw.common.SetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis-string访问接口
 *
 * @author suhq
 * @date 2021-02-25 15:28
 **/
@Slf4j
//同一个类支持支持多个RedisValueUtil注解
@Primary
@Component
public class RedisValueUtil<T> extends RedisBase {

    private ValueOperations<String, T> getVop(String key) {
        return (ValueOperations<String, T>) getTemplate(key).opsForValue();
    }

    public T get(int key) {
        return (T) this.get(String.valueOf(key));
    }

    public T get(long key) {
        return (T) this.get(String.valueOf(key));
    }

    public Long increment(String key, long delta) {
        return getVop(key).increment(key, delta);
    }

    /**
     * 单个获取，如果有批量操作，请使用getBatch方法
     *
     * @param key
     * @return
     */
    @Nullable
    public T get(String key) {
        return (T) getVop(key).get(key);
    }

    @SuppressWarnings("unchecked")
    public T get(String key, Class<T> clazz) {
        Object valueObj = getVop(key).get(key);
        if (clazz.isInstance(valueObj)) {
            return (T) valueObj;
        } else if (clazz == Long.class && valueObj instanceof Integer) {
            Integer obj = (Integer) valueObj;
            return (T) Long.valueOf(obj.longValue());
        }
        return null;
    }

    /**
     * 批量获取
     *
     * @param keys !!!检查同一数据源
     * @return
     */
    @NonNull
    public List<T> getBatch(Collection<String> keys) {
        if (null == keys || keys.size() == 0) {
            return new ArrayList<>();
        }
        if (null != keys && keys.size() > 10000) {
            log.warn("一次性获取的数据太多！数据量：{}", keys.size());
        }
        try {
            return Optional.ofNullable(getVop(keys.iterator().next()).multiGet(keys)).orElse(new ArrayList<T>(0));
        } catch (Exception e) {
            //批量获取失败，逐条获取
            List<T> list = new ArrayList<T>();
            for (String key : keys) {
                T data = getSingle(key);
                if (null != data) {
                    list.add(data);
                }
            }
            return list;
        }
    }

    private T getSingle(String key) {
        try {
            return (T) getVop(key).get(key);
        } catch (Exception e) {
            log.error("redis key={} 对象获取失败！", key);
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @NonNull
    public List<T> get(String... keys) {
        if (null != keys && keys.length > 10000) {
            log.error("一次性获取的数据太多！数据量：" + keys.length);
        }
        List<T> list = new ArrayList<T>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }

    /**
     * 批量设置
     *
     * @param map !!!同一数据源
     */
    public void multiSet(Map<String, T> map) {
        if (SetUtil.isEmpty(map.keySet())) {
            return;
        }
        getVop(map.keySet().iterator().next()).multiSet(map);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(int key, T value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(long key, T value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(String key, T value) {
        getVop(key).set(key, value);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 过期时间 -单位s
     * @return
     */
    public void set(String key, T value, Long expireTime) {
        getVop(key).set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public void set(String key, T value, long timeout, TimeUnit unit) {
        getVop(key).set(key, value, timeout, unit);
    }

    /**
     * set if not exists，当key不存在的时候set进去返回true，key存在直接返回false
     *
     * @param key
     * @param value
     * @param timeOut
     * @param unit
     * @return
     */
    public Boolean setNX(String key, T value, long timeOut, TimeUnit unit) {
        return getVop(key).setIfAbsent(key, value, timeOut, unit);
    }

    public Boolean setNX(String key, T value, long milliseconds) {
        return getVop(key).setIfAbsent(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }
}