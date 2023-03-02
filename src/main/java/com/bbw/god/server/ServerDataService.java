package com.bbw.god.server;

import com.bbw.common.ListUtil;
import com.bbw.common.SetUtil;
import com.bbw.god.db.pool.ServerDataPool;
import com.bbw.god.server.redis.ServerRedisKey;
import com.bbw.god.server.redis.ServerRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 区服数据
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-26 14:25
 */
@Slf4j
@Service("serverDataService")
@Primary
public class ServerDataService {
    @Autowired
    private ServerRedisUtil redis;
    @Autowired
    private ServerDataPool dataPool;

    @Nullable
    public ServerData getServerData(int sid, ServerDataType resType, Long dataId) {
        return redis.fromRedis(sid, resType, dataId);
    }

    @Nullable
    public <T extends ServerData> T getServerData(int sid, Class<T> clazz, Long dataId) {
        T obj = redis.fromRedis(sid, clazz, dataId);
        if (null == obj && !clazz.getSimpleName().equals("FstRanking")) {
            String msg = "不存在的对象。sid=[" + sid + "] Class=[" + clazz + "] dataId=[" + dataId + "]";
            log.error(msg, new Exception());
        }
        return obj;
    }

    public <T extends ServerData> void addServerData(List<T> dataList) {
        if (null == dataList || dataList.isEmpty()) {
            return;
        }
        redis.toRedis(dataList);
        Set<String> keys = new HashSet<>(dataList.size());
        for (ServerData data : dataList) {
            keys.add(ServerRedisKey.getServerDataKey(data));
        }
        if (SetUtil.isEmpty(keys)) {
            return;
        }
        // 玩家ID.资源类型.资源ID
        String[] values = keys.toArray(new String[0]);
        dataPool.toInsertPool(values);
    }

    public void addServerData(ServerData data) {
        redis.toRedis(data);
        dataPool.toInsertPool(ServerRedisKey.getServerDataKey(data));
    }

    /**
     * 更新区服数据
     *
     * @param data
     */
    public void updateServerData(ServerData data) {
        redis.toRedis(data);
        dataPool.toUpdatePool(ServerRedisKey.getServerDataKey(data));
    }

    public <T extends ServerData> void updateServerData(List<T> dataList) {
        redis.toRedis(dataList);
        Set<String> keys = new HashSet<>(dataList.size());
        for (ServerData data : dataList) {
            keys.add(ServerRedisKey.getServerDataKey(data));
        }
        if (SetUtil.isEmpty(keys)) {
            return;
        }
        // 玩家ID.资源类型.资源ID
        String[] values = keys.toArray(new String[0]);
        dataPool.toUpdatePool(values);
    }

    /**
     * 删除区服数据
     *
     * @param data
     */
    public void deleteServerData(ServerData data) {
        redis.deleteFromRedis(data);
        dataPool.toDeletePool(ServerRedisKey.getServerDataKey(data));
    }

    public <T extends ServerData> void deleteServerDatas(List<T> datas, Class clazz) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        Map<Integer, List<T>> dataGroup = datas.stream().collect(Collectors.groupingBy(ServerData::getSid));
        for (Integer sid : dataGroup.keySet()) {
            List<Long> dataIds = dataGroup.get(sid).stream().map(ServerData::getId).collect(Collectors.toList());
            deleteServerDatas(sid, dataIds, clazz);
        }
    }

    /**
     * 删除区服数据
     *
     * @param sid
     * @param dataIds
     * @param clazz
     * @param loopKey
     * @param <T>
     */
    public <T extends ServerData> void deleteServerDatas(int sid, List<Long> dataIds, Class<T> clazz,
                                                         String... loopKey) {
        Set<String> keys = redis.deleteFromRedis(sid, dataIds, clazz, loopKey);
        if (SetUtil.isEmpty(keys)) {
            return;
        }
        String[] values = keys.toArray(new String[0]);
        dataPool.toDeletePool(values);
    }

    public <T extends ServerData> void deleteServerDatas(int sid, Class<T> objClass, String... loopKey) {
        Set<String> keys = redis.deleteFromRedis(sid, objClass, loopKey);
        if (SetUtil.isEmpty(keys)) {
            return;
        }
        String[] values = keys.toArray(new String[0]);
        dataPool.toDeletePool(values);
    }

    /**
     * 返回区服的某一类业务数据。如果没有符合的数据，返回一个empty的List。
     *
     * @return
     */
    @NonNull
    public <T extends ServerData> List<T> getServerDatas(int sid, Class<T> clazz, String... loopKey) {
        List<T> datas = redis.fromRedis(sid, clazz, loopKey);
        // 去除可能的空值
        if (ListUtil.isNotEmpty(datas)) {
            datas = datas.stream().filter(tmp -> tmp != null).collect(Collectors.toList());
        }
        if (ListUtil.isEmpty(datas)) {
            datas = new ArrayList();
        }
        return datas;
    }

}
