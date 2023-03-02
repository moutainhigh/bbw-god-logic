package com.bbw.god.gameuser.redis;

import com.alibaba.fastjson.JSON;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameUser属性的redis操作帮助类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-30 09:43
 */
@Service
public class GameUserDataRedisUtil {
    private static final String DB_LOAD_KEY = "load";
    @Autowired
    private UserDataBatchRedis userDataBatchRedis;// 批量处理玩家数据
    @Autowired
    private RedisValueUtil<UserData> userDataRedis;// 玩家资源数据对象，存放UserData对象
    @Autowired
    private RedisSetUtil<String> userDataTypeSetRedis;// 玩家资源数据类型集合，存放某一数据类型的ID集
    @Autowired
    private InsRoleInfoService roleInfoService;
    @Autowired
    private RedisValueUtil<String> statusRedis;// 玩家资源数据是否从mysql载入的状态标志

    @Autowired
    private RedisSetUtil<String> statusSetRedis;// 玩家资源数据是否从mysql载入的状态标志

    /**
     * 批量删除数据
     *
     * @param uid
     * @param ids
     * @param clazz
     */
    public <T extends UserData> Set<String> deleteFromRedis(Long uid, List<Long> ids, Class<T> clazz) {
        UserDataType dataType = UserDataType.fromClass(clazz);
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, clazz);
        }
        if (null == ids || ids.isEmpty()) {
            return new HashSet<>();
        }
        // 删除对象
        String dataTypeKey = UserRedisKey.getDataTypeKey(uid, dataType);
        Set<String> keys = new HashSet<>();
        for (Long dataId : ids) {
            keys.add(UserRedisKey.getUserDataKey(uid, dataType, dataId));
        }
        userDataRedis.delete(keys);
        userDataTypeSetRedis.remove(dataTypeKey, keys);
        return keys;
    }

    public void deleteFromRedis(UserData data) {
        if (!hasLoadFromDb(data.getGameUserId(), data.gainResType())) {
            dbLoadToRedis(data.getGameUserId(), data.gainResType().getEntityClass());
        }
        // 删除对象
        String dataTypeKey = UserRedisKey.getDataTypeKey(data);
        String dataKey = UserRedisKey.getUserDataKey(data);
        userDataRedis.delete(dataKey);
        userDataTypeSetRedis.remove(dataTypeKey, dataKey);
    }

    /**
     * 获取一个玩家某个数据类型的redis键值
     *
     * @param <T>
     * @param uid
     * @param clazz
     * @return
     */
    public <T extends UserData> Set<String> getUserDataKeys(Long uid, Class<T> clazz) {
        Set<String> keys = new HashSet<>();
        UserDataType dataType = UserDataType.fromClass(clazz);
        String dataTypeKey = UserRedisKey.getDataTypeKey(uid, dataType);
        // 数据类型集合对象key
        keys.add(dataTypeKey);
        // 数据类型集合
        Set<String> ids = userDataTypeSetRedis.members(dataTypeKey);
        if (null != ids && !ids.isEmpty()) {
            keys.addAll(ids);
        }
        return keys;
    }

    /**
     * 获取一个玩家某个数据类型其他相关的redis键值，如变量值
     *
     * @param <T>
     * @param uid
     * @param clazz
     * @return
     */
    public <T extends UserData> Set<String> getUserDataRelatedKeys(Long uid, Class<T> clazz) {
        Set<String> keys = new HashSet<>();
        UserDataType dataType = UserDataType.fromClass(clazz);
        // 是否载入的标识符
        String hasLoadKey = getHasLoadKey(uid, dataType);
        keys.add(hasLoadKey);
        String loadStatusKey = getLoadStatusKey(uid); // getHasLoadKey(uid, dataType);
        keys.add(loadStatusKey);
        return keys;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean exists(String key) {
        return userDataRedis.exists(key);
    }

    /**
     * 获取玩家某一数据类型的所有数据。如果没有符合的数据，返回一个empty的List。
     *
     * @param uid
     * @param clazz
     * @return
     */
    @NonNull
    public <T extends UserData> List<T> fromRedis(Long uid, Class<T> clazz) {
        UserDataType dataType = UserDataType.fromClass(clazz);
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        String typeKey = UserRedisKey.getDataTypeKey(uid, dataType);
        Set<String> keys = userDataTypeSetRedis.members(typeKey);
        List<UserData> datas = fromRedisBatch(uid, UserDataType.fromClass(clazz), keys);
        ArrayList<T> result = new ArrayList<T>();
        if (ListUtil.isNotEmpty(datas)) {
            datas.forEach(data -> {
                if (data != null) {
                    result.add(clazz.cast(data));
                }
            });
        }
        return result;
    }

    @Nullable
    public <T extends UserData> T fromRedis(Long uid, Class<T> clazz, Long resId) {
        UserDataType dataType = UserDataType.fromClass(clazz);
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        String key = UserRedisKey.getUserDataKey(uid, dataType, resId);
        UserData data = userDataRedis.get(key);
        if (null != data) {
            return clazz.cast(data);
        }
        return null;
    }

    /**
     * 获取多条记录
     *
     * @param uid
     * @param clazz
     * @param dataIds
     * @param <T>
     * @return
     */
    public <T extends UserData> List<T> fromRedis(Long uid, Class<T> clazz, Collection<Long> dataIds) {
        UserDataType dataType = UserDataType.fromClass(clazz);
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        List<String> keys = dataIds.stream().map(tmp -> UserRedisKey.getUserDataKey(uid, dataType, tmp)).collect(Collectors.toList());
        List<UserData> datas = fromRedisBatch(uid, UserDataType.fromClass(clazz), keys);
        ArrayList<T> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(datas)) {
            datas.forEach(data -> {
                if (data != null) {
                    result.add(clazz.cast(data));
                }
            });
        }
        return result;
    }

    @Nullable
    public UserData fromRedis(Long uid, UserDataType dataType, Long resId) {
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        String key = UserRedisKey.getUserDataKey(uid, dataType, resId);
        return userDataRedis.get(key);
    }

    /**
     * 批量获取
     *
     * @param uid
     * @param dataType
     * @param keys
     * @return
     */
    private List<UserData> fromRedisBatch(Long uid, UserDataType dataType, Collection<String> keys) {
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        List<UserData> datas = userDataRedis.getBatch(keys);
        return datas;
    }

    public <T extends UserData> Long getSize(Long uid, Class<T> clazz) {
        UserDataType dataType = UserDataType.fromClass(clazz);
        if (!hasLoadFromDb(uid, dataType)) {
            dbLoadToRedis(uid, dataType.getEntityClass());
        }
        String typeKey = UserRedisKey.getDataTypeKey(uid, dataType);
        if (userDataTypeSetRedis.exists(typeKey)) {
            return userDataTypeSetRedis.size(typeKey);
        }
        return 0L;
    }

    // 已经从mysql载入过
    public boolean hasLoadFromDb(Long uid, UserDataType dataType) {
        String statusSetKey = getLoadStatusKey(uid);
        boolean hasLoad = statusSetRedis.isMember(statusSetKey, dataType.getRedisKey());
        if (!hasLoad) {
            // 兼容旧数据
            String dataKey = getHasLoadKey(uid, dataType);
            hasLoad = statusRedis.exists(dataKey);
            // 按旧状态加载，则删除旧数据，加入状态集合
            if (hasLoad) {
                statusSetRedis.add(statusSetKey, dataType.getRedisKey());
                if (statusSetRedis.isMember(statusSetKey, dataType.getRedisKey())) {
                    statusRedis.delete(dataKey);
                }
            }
        }
        return hasLoad;
    }

    /**
     * 获取载入到Redis的数据类型数
     *
     * @param uid
     * @return
     */
    public long getLoadedDataTypeNum(long uid) {
        String statusSetKey = getLoadStatusKey(uid);
        Long num = statusSetRedis.size(statusSetKey);
        if (null == num) {
            return -1;
        }
        return num;
    }

    private String getLoadStatusKey(Long uid) {
        String dataKey = UserRedisKey.getRunTimeVarKey(uid, DB_LOAD_KEY);
        return dataKey;
    }

    private String getHasLoadKey(Long uid, UserDataType dataType) {
        String loadKey = DB_LOAD_KEY + UserRedisKey.SPLIT + dataType.getRedisKey();
        String dataKey = UserRedisKey.getRunTimeVarKey(uid, loadKey);
        return dataKey;
    }

    // 从数据库载入
    private <T extends UserData> void dbLoadToRedis(Long uid, Class<T> clazz) {
        int sid = ServerTool.getActiveSid(uid);
        PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
        UserDataType dataType = UserDataType.fromClass(clazz);
        List<InsUserDataEntity> entityList = pdd.dbSelectUserDataByType(uid, dataType.getRedisKey());
        toRedisAsClazz(uid, entityList, clazz);
    }

    public void setLoadStatus(Long uid, UserDataType dataType) {
        // String loadKey = DB_LOAD_KEY + UserRedisKey.SPLIT + dataType.getRedisKey();
        // String dataKey = UserRedisKey.getRunTimeVarKey(uid, loadKey);
        // statusRedis.set(dataKey, "1");
        String loadKey = getLoadStatusKey(uid);
        statusSetRedis.add(loadKey, dataType.getRedisKey());
    }

    /**
     * 是否加载某一类数据到Redis
     *
     * @param uid
     * @param dataType
     * @return
     */
    public boolean hasRoaded(long uid, UserDataType dataType) {
        String loadKey = getLoadStatusKey(uid);
        return statusSetRedis.isMember(loadKey, dataType.getRedisKey());
    }

    /**
     * 批量添加玩家数据。玩家可以不同，数据类型也可以不同
     *
     * @param dataList
     */
    public <T extends UserData> void toRedis(List<T> dataList) {
        userDataBatchRedis.batchSetUserData(dataList);
    }

    public void toRedis(UserData data) {
        if (!hasLoadFromDb(data.getGameUserId(), data.gainResType())) {
            dbLoadToRedis(data.getGameUserId(), data.gainResType().getEntityClass());
        }
        // 分发当前数据条目
        String dataTypeKey = UserRedisKey.getDataTypeKey(data);
        String dataKey = UserRedisKey.getUserDataKey(data);
        userDataRedis.set(dataKey, data);
        userDataTypeSetRedis.add(dataTypeKey, dataKey);
    }

    public <T extends UserData> void toRedisAsClazz(Long uid, List<InsUserDataEntity> datas, Class<T> clazz) {
        List<UserData> dataList = new ArrayList<>();
        for (InsUserDataEntity entity : datas) {
            T userData = JSON.parseObject(entity.getDataJson(), clazz);
            dataList.add(userData);
        }
        userDataBatchRedis.batchSetUserDataFromDb(dataList);
        UserDataType dataType = UserDataType.fromClass(clazz);
        setLoadStatus(uid, dataType);
    }
}
