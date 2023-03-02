package com.bbw.db.redis;

import com.bbw.db.redis.ds.MultiRedis;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis访问基类
 *
 * @author suhq
 * @date 2021-02-25 15:47
 **/
@Slf4j
@Service
public class RedisBase {
    /**
     * 不设置过期时长
     */
    protected final static long NOT_EXPIRE = -1;
    /**
     * 出异常，重复操作的次数
     */
    protected final static Integer RERY_TIMES = 5;

    @Autowired
    protected MultiRedis multiRedis;

    /**
     * 获取key对应的模板
     *
     * @param key
     * @return
     */
    protected RedisTemplate<String, ?> getTemplate(String key) {
        return multiRedis.getRedisTemplate(key);
    }

    /**
     * 获取key对应的Redis服务器
     *
     * @param key
     * @return
     */
    protected String getRedisMark(String key) {
        return multiRedis.getRedisMark(key);
    }

    /**
     * key是否在同一redis服务器
     *
     * @param keys
     * @return
     */
    protected boolean isSameRedisMark(String... keys) {
        List<String> keysToCheck = Arrays.asList(keys);
        return isSameRedisMark(keysToCheck);
    }

    /**
     * 将keys按redis服务器分组
     *
     * @param keys
     * @return
     */
    protected boolean isSameRedisMark(@NonNull Collection<String> keys) {
        if (keys.size() == 0) {
            return false;
        }
        long redisMarkNum = keys.stream().map(tmp -> getRedisMark(tmp)).distinct().count();
        return redisMarkNum == 1;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean exists(String key) {
//        System.out.println("==============redis base do exists.key:" + key);
        try {
            return Optional.ofNullable(getTemplate(key).hasKey(key)).orElse(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param seconds 时间(秒)
     * @return
     */
    public boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                getTemplate(key).expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取key的类型
     *
     * @param key
     * @return
     */
    public DataType getType(String key) {
        return getTemplate(key).type(key);
    }

    /**
     * 批量删除
     *
     * @param keys
     */
    public void delete(Collection<String> keys) {
        Map<String, List<String>> redisMarkKeyMap = keys.stream().collect(Collectors.groupingBy(tmp -> getRedisMark(tmp)));
        if (redisMarkKeyMap.isEmpty()) {
            return;
        }
        for (String redisMark : redisMarkKeyMap.keySet()) {
            List<String> toDels = redisMarkKeyMap.get(redisMark);
            getTemplate(toDels.get(0)).delete(toDels);
        }

    }

    /**
     * 删除缓存
     * 根据key精确匹配删除
     *
     * @param key
     */
    public Boolean delete(String key) {
        return getTemplate(key).delete(key);
    }

    /**
     * 生产环境慎用。效率低。
     * <pre>
     * 根据redis支持的正则表达式来移除key-value
     * redis中允许模糊查询的只有3个通配符，分别是：*，?，[]
     * *: 通配任意多个字符
     * ?: 通配单个字符
     * []: 通配括号内的某1个字符
     * </pre>
     *
     * @param blear
     */
    public void deleteBlear(String blear) {
        int maxCount = 10000;//符合条件的数量不允许超过此值
        Set<String> keys = scan(blear, maxCount);
        RedisTemplate<String, ?> redisTemplate = getTemplate(blear);
        redisTemplate.delete(keys);
    }

    /**
     * 生产环境慎用。效率低。
     * <pre>
     * 根据redis支持的正则表达式来移除key-value
     * redis中允许模糊查询的只有3个通配符，分别是：*，?，[]
     * *: 通配任意多个字符
     * ?: 通配单个字符
     * []: 通配括号内的某1个字符
     * </pre>
     *
     * @param blear
     */
    public Set<String> scan(String blear, int count) {
        RedisTemplate<String, ?> redisTemplate = getTemplate(blear);
        //使用pipeline方式
        Set<String> keys = redisTemplate.execute(new RedisCallback<Set<String>>() {
            Set<String> binaryKeys = new HashSet<>();

            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                ScanOptions options = ScanOptions.scanOptions().match(blear).count(count).build();
                Cursor<byte[]> cursor = connection.scan(options);
                try {
                    while (cursor.hasNext() && binaryKeys.size() < count) {
                        binaryKeys.add(new String(cursor.next()));
                    }
                } finally {
                    try {
                        cursor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return binaryKeys;
            }
        });
        return keys;
    }

    /**
     * 修改key名 如果不存在该key或者没有修改成功返回false。
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        //如果两者不在同一台redis服务器，不做操作
        if (!isSameRedisMark(oldKey, newKey)) {
            return false;
        }
        return getTemplate(newKey).renameIfAbsent(oldKey, newKey);
    }
}