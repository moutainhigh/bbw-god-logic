package com.bbw.god.gameuser.redis;

import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.db.redis.serializer.FastJsonRedisSerializer;
import com.bbw.god.gameuser.UserData;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
class UserDataBatchRedis extends RedisValueUtil<UserData> {

    public <T extends UserData> void batchSetUserDataFromDb(List<T> dataList) {
        batchSetUserData(dataList, true);
    }

    public <T extends UserData> void batchSetUserData(List<T> dataList) {
        batchSetUserData(dataList, false);
    }

    private <T extends UserData> void batchSetUserData(List<T> dataList, boolean fromDb) {
        if (ListUtil.isEmpty(dataList)) {
            return;
        }
        Map<Long, List<T>> userDatas = dataList.stream().collect(Collectors.groupingBy(T::getGameUserId));
        long begin = System.currentTimeMillis();
        for (Long uid : userDatas.keySet()) {
            doBatchSetUserData(userDatas.get(uid), fromDb);
        }
        log.debug("批量添加玩家数据，耗时：{}", System.currentTimeMillis() - begin);
    }

    private <T extends UserData> void doBatchSetUserData(List<T> dataList, boolean fromDb) {
        if (ListUtil.isEmpty(dataList)) {
            return;
        }
        RedisTemplate redisTemplate = multiRedis.getRedisTemplate(UserRedisKey.getUserDataKey(dataList.get(0)));
        // 使用pipeline方式
        redisTemplate.executePipelined(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(RedisConnection connection) throws DataAccessException {
                // 必须和 RedisConfig 设置的一致
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                FastJsonRedisSerializer<Object> fastJson = new FastJsonRedisSerializer<>(Object.class);
                for (T data : dataList) {
                    String typeKey = UserRedisKey.getDataTypeKey(data);
                    byte[] rawTypeKey = keySerializer.serialize(typeKey);
                    String key = UserRedisKey.getUserDataKey(data);
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
