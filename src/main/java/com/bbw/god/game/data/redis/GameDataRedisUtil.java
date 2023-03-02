package com.bbw.god.game.data.redis;

import com.alibaba.fastjson.JSON;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.InsGameDataEntity;
import com.bbw.god.db.service.InsGameDataService;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 全服数据redis 操作
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 15:01
 */
@Component
public class GameDataRedisUtil {
    private static final String DB_LOAD_KEY = "load";
    @Autowired
    private RedisHashUtil<String, GameData> gameDataHashRedis;
    @Autowired
    private InsGameDataService dataService;
    @Autowired
    private RedisValueUtil<String> statusRedis;// 全局数据是否从mysql载入的状态标志
    // 已经从mysql载入过

    /**
     * 数据量
     *
     * @param dataType
     * @return
     */
    public long dataCount(GameDataType dataType, String... loopKey) {
        String typeKey = GameRedisKey.getDataTypeKey(dataType, loopKey);
        return gameDataHashRedis.getSize(typeKey);
    }

    // 从数据库载入
    private <T extends GameData> void dbLoadToRedis(Class<T> clazz) {
        GameDataType dataType = GameDataType.fromClass(clazz);
        List<InsGameDataEntity> entityList = dataService.selectByDataType(dataType.getRedisKey());
        List<GameData> dataList = new ArrayList<>();
        for (InsGameDataEntity entity : entityList) {
            T data = JSON.parseObject(entity.getDataJson(), clazz);
            dataList.add(data);
        }
        if (ListUtil.isNotEmpty(dataList)) {
            toRedis(dataList);
            setLoadStatus(dataType);
        }

    }

    public void deleteGameData(GameData data, String... loopKey) {
        String dataTypeKey = GameRedisKey.getDataTypeKey(data.gainDataType(), loopKey);
        String key = GameRedisKey.getGameDataKey(data);
        gameDataHashRedis.removeField(dataTypeKey, key);
    }

    /**
     * 批量删除数据
     *
     * @param ids
     * @param clazz
     */
    public <T extends GameData> String[] deleteGameDatas(List<Long> dataIds, Class<T> clazz, String... loopKey) {
        if (null == dataIds || dataIds.isEmpty()) {
            return new String[0];
        }
        // 删除对象
        GameDataType dataType = GameDataType.fromClass(clazz);
        String dataTypeKey = GameRedisKey.getDataTypeKey(dataType, loopKey);
        String[] keys = new String[dataIds.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = GameRedisKey.getGameDataKey(dataType, dataIds.get(i));
        }
        gameDataHashRedis.removeField(dataTypeKey, keys);
        return keys;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean exists(String key) {
        return gameDataHashRedis.exists(key);
    }

    /**
     * 获取区服某一类型数据。如果没有符合的数据，返回一个empty的List。
     *
     * @param clazz
     * @return
     */
    public <T extends GameData> List<T> fromRedis(Class<T> clazz, String... loopKey) {
        GameDataType dataType = GameDataType.fromClass(clazz);
        if (!hasLoadFromDb(dataType)) {
            dbLoadToRedis(dataType.getEntityClass());
        }
        String typeKey = GameRedisKey.getDataTypeKey(dataType, loopKey);
        List<GameData> datas = gameDataHashRedis.getFieldValueList(typeKey);
        ArrayList<T> result = new ArrayList<T>();
        if (ListUtil.isNotEmpty(datas)) {
            datas.forEach(data -> result.add(clazz.cast(data)));
        }
        return result;
    }

    /**
     * 从redis中获取对象
     *
     * @param dataId
     * @return
     */
    public GameData fromRedis(GameDataType dataType, Long dataId, String... loopKey) {
        if (!hasLoadFromDb(dataType)) {
            dbLoadToRedis(dataType.getEntityClass());
        }
        String typeKey = GameRedisKey.getDataTypeKey(dataType, loopKey);
        String key = GameRedisKey.getGameDataKey(dataType, dataId);
        return gameDataHashRedis.getField(typeKey, key);
    }

    /**
     * 从redis中获取对象
     *
     * @param clazz
     * @param dataId
     * @return
     */
    public <T extends GameData> T fromRedis(Long dataId, Class<T> clazz, String... loopKey) {
        GameDataType dataType = GameDataType.fromClass(clazz);
        if (!hasLoadFromDb(dataType)) {
            dbLoadToRedis(dataType.getEntityClass());
        }
        String typeKey = GameRedisKey.getDataTypeKey(dataType, loopKey);
        String key = GameRedisKey.getGameDataKey(dataType, dataId);
        return clazz.cast(gameDataHashRedis.getField(typeKey, key));
    }

    /**
     * 批量获取UserData 数据
     *
     * @param keys: ServerData的redis 键值。格式：区服rediskey.资源类型.资源ID
     * @return
     */
    public List<GameData> fromRedisBatch(GameDataType resType, Collection<String> keys, String... loopKey) {
        String typeKey = GameRedisKey.getDataTypeKey(resType, loopKey);
        List<GameData> datas = gameDataHashRedis.getFieldBatch(typeKey, keys);
        return datas;
    }

    private boolean hasLoadFromDb(GameDataType dataType) {
        String loadKey = DB_LOAD_KEY + ServerRedisKey.SPLIT + dataType.getRedisKey();
        String dataKey = GameRedisKey.getRunTimeVarKey(loadKey);
        return statusRedis.exists(dataKey);
    }

    private void setLoadStatus(GameDataType dataType) {
        String loadKey = DB_LOAD_KEY + ServerRedisKey.SPLIT + dataType.getRedisKey();
        String dataKey = GameRedisKey.getRunTimeVarKey(loadKey);
        statusRedis.set(dataKey, "1");
    }

    /**
     * 将区服数据保存到redis
     *
     * @param data
     */
    public void toRedis(GameData data, String... loopKey) {
        String typeKey = GameRedisKey.getDataTypeKey(data.gainDataType(), loopKey);
        String key = GameRedisKey.getGameDataKey(data);
        gameDataHashRedis.putField(typeKey, key, data);
    }

    /**
     * 批量保存
     *
     * @param datas
     */
    public <T extends GameData> void toRedis(List<T> datas) {
        String typeKey = GameRedisKey.getDataTypeKey(datas.get(0).gainDataType());
        Map<String, GameData> map = new HashMap<>();
        for (GameData data : datas) {
            String dataKey = GameRedisKey.getGameDataKey(data);
            map.put(dataKey, data);
        }
        gameDataHashRedis.putAllField(typeKey, map);
    }
}
