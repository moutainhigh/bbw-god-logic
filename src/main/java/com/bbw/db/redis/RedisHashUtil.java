package com.bbw.db.redis;

import com.bbw.common.ListUtil;
import com.bbw.common.RegexPatternHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * redis-hash访问接口
 *
 * @author suhq
 * @date 2021-02-25 15:42
 **/
@Component
public class RedisHashUtil<HK, HV> extends RedisBase {
    private HashOperations<String, HK, HV> getHop(String key) {
        return getTemplate(key).opsForHash();
    }

    /**
     * 添加map
     *
     * @param key
     * @param map
     */
    public void putAllField(String key, Map<HK, HV> map) {
        getHop(key).putAll(key, map);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     */
    public void putField(String key, HK field, HV value) {
        getHop(key).put(key, field, value);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param time  过期时间-整个MAP的过期时间
     * @param value 值
     */
    public void putField(String key, HK field, HV value, long time) {
        getHop(key).put(key, field, value);
        expire(key, time);
    }

    /**
     * 判断map中对应key的key是否存在
     *
     * @param key map对应的key
     * @return
     */
    public Boolean hasField(String key, HK field) {
        return getHop(key).hasKey(key, field);
    }

    /**
     * 获取map对象
     *
     * @param key map对应的key
     * @return
     */
    public Map<HK, HV> get(String key) {
        return getHop(key).entries(key);
    }

    /**
     * 获取map缓存中的某个对象
     *
     * @param key      map对应的key
     * @param fieldKey map中该对象的key
     * @return
     */
    @Nullable
    public HV getField(String key, HK fieldKey) {
        return getHop(key).get(key, fieldKey);
    }

    /**
     * 批量获取，结果顺序和键值顺序一致。如果没有取到对象，对应null
     *
     * @param key
     * @param fieldKeys
     * @return
     */
    public List<HV> getFieldBatch(String key, Collection<HK> fieldKeys) {
        return getHop(key).multiGet(key, fieldKeys);
    }

    /**
     * 如果对应的key在Redis没有值，则对应的数据为空map{}
     *
     * @param keys !!!必须是同一数据源
     * @return
     */
    public Map<String, Map<HK, HV>> getBatch(List<String> keys) {
        Map<String, Map<HK, HV>> results = new HashMap<>();
        if (ListUtil.isEmpty(keys)) {
            return results;
        }
        List<Object> redisResults = this.multiRedis.getRedisTemplate(keys.get(0)).executePipelined(new RedisCallback<Map<HK, HV>>() {
            @Override
            public Map<HK, HV> doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys) {
                    connection.hGetAll(key.getBytes());
                }
                return null;
            }
        });
        if (ListUtil.isNotEmpty(redisResults)) {
            for (int i = 0; i < redisResults.size(); i++) {
                results.put(keys.get(i), (Map<HK, HV>) redisResults.get(i));
            }
        }
        return results;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public long increment(String key, HK item, long by) {
        return getHop(key).increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public long decrement(String key, HK item, int by) {
        return getHop(key).increment(key, item, -by);
    }

    /**
     * 获取指定key对map对象
     *
     * @param key
     * @return
     */
    public Map<HK, HV> entries(String key) {
        return getHop(key).entries(key);
    }

    /**
     * 获取map的key
     *
     * @param key map对应的key
     * @return
     */
    public Set<HK> getFieldKeySet(String key) {
        return getHop(key).keys(key);
    }

    /**
     * 获取map对应key的value
     *
     * @param key map对应的key
     * @return
     */
    public List<HV> getFieldValueList(String key) {
        return getHop(key).values(key);
    }

    /**
     * 获取map对象
     *
     * @param key map对应的key
     * @return
     */
    public Long getSize(String key) {
        return getHop(key).size(key);
    }

    /**
     * 删除map中的某个对象
     *
     * @param key      map对应的key
     * @param fieldKey map中该对象的key
     */
    public void removeField(String key, HK... fieldKey) {
        getHop(key).delete(key, fieldKey);
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param regexs
     */
    public void removeFieldByRegular(String key, String... regexs) {
        for (String regex : regexs) {
            removeFieldByRegular(key, regex);
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param regex
     */
    public void removeFieldByRegular(String key, String regex) {
        Map<HK, HV> map = get(key);
        Set<HK> stringSet = map.keySet();
        Pattern r = Pattern.compile(regex);
        for (HK s : stringSet) {
            if (r.matcher(s.toString()).matches()) {
                getHop(key).delete(key, s);
            }
        }
    }

    /**
     * 模糊删除。直接支持*号
     *
     * @param key
     * @param blear
     */
    public void removeFieldByBlear(String key, String blear) {
        String pattern = RegexPatternHelper.compile(blear);
        removeFieldByRegular(key, pattern);
    }
}