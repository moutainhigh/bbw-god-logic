package com.bbw.god.server.redis;

import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.db.redis.serializer.FastJsonRedisSerializer;
import com.bbw.god.server.ServerData;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-01 23:31
 */
@Service
class ServerDataBatchRedis extends RedisValueUtil<ServerData> {
    public <T extends ServerData> void batchSetServerDataFromDb(List<T> dataList) {
        batchSetServerData(dataList, true);
    }

    public <T extends ServerData> void batchSetServerData(List<T> dataList) {
        batchSetServerData(dataList, false);
    }

    private <T extends ServerData> void batchSetServerData(List<T> dataList, boolean fromDb) {
        if (ListUtil.isEmpty(dataList)) {
            return;
        }
        Map<Integer, List<T>> serverDatas = dataList.stream().collect(Collectors.groupingBy(T::getSid));
        for (Integer sid : serverDatas.keySet()) {
            doBatchSetServerData(serverDatas.get(sid), fromDb);
        }
    }

    private <T extends ServerData> void doBatchSetServerData(List<T> dataList, boolean fromDb) {
        if (ListUtil.isEmpty(dataList)) {
            return;
        }
        RedisTemplate redisTemplate = multiRedis.getRedisTemplate(ServerRedisKey.getServerDataKey(dataList.get(0)));
        // 使用pipeline方式
        redisTemplate.executePipelined(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
                // 必须和 RedisConfig 设置的一致
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                FastJsonRedisSerializer<Object> fastJson = new FastJsonRedisSerializer<Object>(Object.class);
                for (T data : dataList) {
                    String typeKey = ServerRedisKey.getDataTypeKey(data.getSid(), data.gainDataType());
                    if (data.isLoopData() && StrUtil.isNotNull(data.getLoopKey())) {
                        typeKey = ServerRedisKey.getDataTypeKey(data.getSid(), data.gainDataType(), data.getLoopKey());
                    }
                    String key = ServerRedisKey.getServerDataKey(data);
                    byte[] rawTypeKey = keySerializer.serialize(typeKey);
                    byte[] rawKey = keySerializer.serialize(key);
                    byte[] dataRaw = fastJson.serialize(data);
                    if (null == rawTypeKey || null == rawKey || null == dataRaw) {
                        continue;
                    }
                    if (fromDb) {
                        // 从数据库载入
                        Boolean redisExist = connection.exists(rawKey);
                        // redis不存在数据才写入
                        if (null == redisExist || !redisExist) {
                            connection.sAdd(rawTypeKey, fastJson.serialize(key));
                            connection.set(rawKey, dataRaw);
                        }
                    } else {
                        connection.sAdd(rawTypeKey, fastJson.serialize(key));
                        connection.set(rawKey, dataRaw);
                    }
                }
                return null;
            }
        });
    }
}
