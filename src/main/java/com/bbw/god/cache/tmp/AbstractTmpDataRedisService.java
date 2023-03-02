package com.bbw.god.cache.tmp;

import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 临时数据持久化到Redis。该接口的数据不会同步到数据库。<br/>
 * 需要自定义Redis存储的的数据继承TmpData,并实现AbstractTmpDataRedisService。
 *
 * @author: suhq
 * @date: 2022/11/15 10:26 上午
 */
@Slf4j
public abstract class AbstractTmpDataRedisService<T extends AbstractTmpData, N extends Number> {
    @Autowired
    private RedisHashUtil<Long, T> redisHashUtil;


    /**
     * 批量获取数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @return
     */
    public List<T> getDatas(N belong, String... loop) {
        Class<T> dataClazz = getDataClazz();
        List<T> datas = fromRedis(belong, dataClazz, loop);
        try {
            if (!datas.isEmpty()) {
                long start = System.currentTimeMillis();

                datas.sort(Comparator.comparing(T::getId));

                long end = System.currentTimeMillis();
                if (end - start > 100) {
                    log.error("{}批量获取{},获取数量{},耗时{}", belong, dataClazz.getSimpleName(), datas.size(), end - start);
                }
            }
        } catch (Exception e) {
            String msg = "获取" + belong + "的" + dataClazz.getSimpleName() + "数据异常！";
            throw CoderException.high(msg, e);
        }
        return datas;
    }

    /**
     * 获取单一数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @return
     */
    public T getSingleData(N belong) {
        Class<T> dataClazz = getDataClazz();
        List<T> datas = getDatas(belong);
        if (datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    /**
     * 获取一条数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param field
     * @param loop
     * @return
     */
    public Optional<T> getData(N belong, long field, String... loop) {
        Class<T> dataClazz = getDataClazz();
        T data = fromRedis(belong, field, dataClazz, loop);
        if (null == data) {
            String msg = "不存在的对象。数据归属=[" + belong + "] Class=[" + dataClazz + "] field=[" + field + "]";
            //出现data为0，意味着业务数据有问题
            if (field == 0) {
                log.error(msg);
            } else {
                log.warn(msg);
            }
            return Optional.empty();
        }
        return Optional.of(data);
    }

    /**
     * 批量获取数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param fields
     * @param loop
     * @return
     */
    public List<T> getDatas(N belong, Collection<Long> fields, String... loop) {
        Class<T> dataClazz = getDataClazz();
        try {
            List<T> objs = fromRedis(belong, fields, dataClazz, loop);
            if (!objs.isEmpty()) {
                long start = System.currentTimeMillis();

                objs.sort(Comparator.comparing(T::getId));

                long end = System.currentTimeMillis();
                if (end - start > 100) {
                    log.error("{}批量获取{},获取数量{},耗时{}", belong, dataClazz.getSimpleName(), objs.size(), end - start);
                }
            }
            return objs;
        } catch (Exception e) {
            String msg = "获取" + belong + "的" + dataClazz.getSimpleName() + "数据异常！";
            throw CoderException.high(msg, e);
        }
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(T data) {
        if (null == data) {
            return;
        }
        toRedis(data);
    }

    /**
     * 批量添加数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param datas
     */
    public void addDatas(N belong, List<T> datas) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        toRedis(datas);
    }


    /**
     * 更新单条数据
     *
     * @param data
     */
    public void updateData(T data) {
        if (null == data) {
            return;
        }
        toRedis(data);
    }

    /**
     * 批量更新数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param datas
     */
    public void updateDatas(N belong, List<T> datas) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        toRedis(datas);
    }

    /**
     * 删除单条数据
     *
     * @param data
     */
    public void deleteData(T data) {
        if (null == data) {
            return;
        }
        deleteFromRedis(data);
    }

    /**
     * 批量删除多条数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param datas
     */
    public void deleteDatas(N belong, List<T> datas) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        Map<String, List<T>> loopGroup = datas.stream().collect(Collectors.groupingBy(tmp -> getDataLoop(tmp)));
        for (Map.Entry<String, List<T>> entry : loopGroup.entrySet()) {
            String loop = entry.getKey();
            List<Long> fields = entry.getValue().stream().map(tmp -> getField(tmp)).collect(Collectors.toList());
            deleteDatas(belong, fields, loop);
        }
    }

    /**
     * 批量删除多条数据
     *
     * @param belong 数据归属者。比如区服组serverGroupId、区服sid、玩家uid
     * @param fields
     */
    public void deleteDatas(N belong, List<Long> fields, String... loop) {
        Class<T> dataClazz = getDataClazz();
        if (ListUtil.isEmpty(fields)) {
            return;
        }
        deleteFromRedis(belong, fields, dataClazz, loop);
    }

    /**
     * 从Redis获取数据如果没有符合的数据，返回一个empty的List。
     *
     * @param belong
     * @param clazz
     * @return
     */
    private List<T> fromRedis(N belong, Class<T> clazz, String... loop) {
        TmpDataType dataType = TmpDataType.fromClass(clazz);
        String key = getRedisKey(belong, dataType, loop);
        List<T> datas = redisHashUtil.getFieldValueList(key);
        if (null == datas) {
            return new ArrayList<>();
        }
        if (datas.size() > 1000) {
            log.warn("一次性获取{}条临时数据{}", datas.size(), clazz.getSimpleName());
        }
        return datas;
    }

    /**
     * 从Redis获取数据如果没有符合的数据，返回一个empty的List。
     *
     * @param belong
     * @param clazz
     * @return
     */
    private List<T> fromRedis(N belong, Collection<Long> fields, Class<T> clazz, String... loop) {
        TmpDataType dataType = TmpDataType.fromClass(clazz);
        String key = getRedisKey(belong, dataType, loop);
        List<T> datas = redisHashUtil.getFieldBatch(key, fields);
        datas = datas.stream().filter(tmp -> null != tmp).collect(Collectors.toList());
        if (null == datas) {
            return new ArrayList<>();
        }
        if (datas.size() > 1000) {
            log.warn("一次性获取{}条临时数据{}", datas.size(), clazz.getSimpleName());
        }
        return datas;
    }

    /**
     * 从redis中获取对象
     *
     * @param belong
     * @param fieldId
     * @param clazz
     * @return
     */
    private T fromRedis(N belong, Long fieldId, Class<T> clazz, String... loop) {
        TmpDataType dataType = TmpDataType.fromClass(clazz);
        String key = getRedisKey(belong, dataType, loop);
        return clazz.cast(redisHashUtil.getField(key, fieldId));
    }

    /**
     * 保存单条数据
     *
     * @param data
     */
    private void toRedis(T data) {
        TmpDataType dataType = TmpDataType.fromClass(data.getClass());
        N belong = getDataBelong(data);
        String loop = getDataLoop(data);
        String key = getRedisKey(belong, dataType, loop);
        Long field = getField(data);
        redisHashUtil.putField(key, field, data);
        long expiredMillis = getExpiredMillis(data);
        if (expiredMillis > 0) {
            redisHashUtil.expire(key, expiredMillis, TimeUnit.MILLISECONDS);
        } else {
            log.warn("{}-{}未设置过期时间", getDataClazz().getSimpleName(), key);
        }
    }

    /**
     * 批量保存
     *
     * @param datas
     */
    private void toRedis(List<T> datas) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        if (datas.size() > 1000) {
            log.warn("一次性保存{}条临时数据{}", datas.size(), datas.get(0).getClass().getSimpleName());
        }
        TmpDataType dataType = TmpDataType.fromClass(datas.get(0).getClass());
        N belong = getDataBelong(datas.get(0));
        Map<String, List<T>> loopGroup = datas.stream().collect(Collectors.groupingBy(tmp -> getDataLoop(tmp)));
        for (Map.Entry<String, List<T>> entry : loopGroup.entrySet()) {
            String key = getRedisKey(belong, dataType, entry.getKey());
            Map<Long, T> map = new HashMap<>();
            for (T data : entry.getValue()) {
                Long field = getField(data);
                map.put(field, data);
            }
            redisHashUtil.putAllField(key, map);
            long expiredMillis = getExpiredMillis(entry.getValue().get(0));
            if (expiredMillis > 0) {
                redisHashUtil.expire(key, expiredMillis, TimeUnit.MILLISECONDS);
            } else {
                log.warn("{}-{}未设置过期时间", getDataClazz().getSimpleName(), key);
            }
        }
    }

    /**
     * 删除单条数据
     *
     * @param data
     */
    private void deleteFromRedis(T data) {
        TmpDataType dataType = TmpDataType.fromClass(data.getClass());
        N belong = getDataBelong(data);
        String loop = getDataLoop(data);
        String key = getRedisKey(belong, dataType, loop);
        Long field = getField(data);
        redisHashUtil.removeField(key, field);
    }

    /**
     * 批量删除数据
     *
     * @param belong
     * @param fields
     * @param clazz
     */
    private void deleteFromRedis(N belong, List<Long> fields, Class<T> clazz, String... loop) {
        if (ListUtil.isEmpty(fields)) {
            return;
        }
        if (fields.size() > 1000) {
            log.warn("一次性移除{}条临时数据{}", fields.size(), clazz.getSimpleName());
        }
        TmpDataType dataType = TmpDataType.fromClass(clazz);
        String key = getRedisKey(belong, dataType, loop);
        Long[] fieldIdsToDel = fields.toArray(new Long[0]);
        redisHashUtil.removeField(key, fieldIdsToDel);
    }


    /**
     * 服务匹配
     *
     * @param dataType
     * @return
     */
    protected boolean isMatch(TmpDataType dataType) {
        return dataType.getEntityClass().equals(getDataClazz());
    }

    /**
     * 获取业务数据类型
     *
     * @return
     */
    protected abstract Class<T> getDataClazz();

    /**
     * 获取数据归属
     *
     * @return
     */
    protected abstract N getDataBelong(T data);

    /**
     * 获取循环数据的循环标识
     *
     * @return
     */
    protected abstract String getDataLoop(T data);

    /**
     * 获取Redis key
     *
     * @param belong
     * @param dataType
     * @return
     */
    protected abstract String getRedisKey(N belong, TmpDataType dataType, String... loop);

    /**
     * 获取Redis hash field
     *
     * @param data
     * @return
     */
    protected abstract Long getField(T data);

    /**
     * 获取过期时间（ms）
     *
     * @return
     */
    protected abstract long getExpiredMillis(T data);
}
