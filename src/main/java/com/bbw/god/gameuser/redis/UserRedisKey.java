package com.bbw.god.gameuser.redis;

import com.bbw.common.ID;
import com.bbw.exception.CoderException;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import org.springframework.lang.NonNull;

/**
 * 富甲封神传ID和redis的Key服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-15 09:28
 */
public class UserRedisKey extends RedisKeyConst {

    @NonNull
    public static String getGameUserKey(Long uid) {
        return getBaseGameUserKey(uid).toString();//"usr" + SPLIT + uid.toString();
    }

    /**
     * 玩家uid
     *
     * @param uid：玩家ID
     * @return
     */
    @NonNull
    public static String getUserStatusKey(Long uid) {
        StringBuilder sb = getBaseGameUserKey(uid);
        sb.append(SPLIT);
        sb.append("0status");
        return sb.toString();//getGameUserKey(uid) + SPLIT + "0status";
    }

    @NonNull
    public static String getUserSettingKey(Long uid) {
        StringBuilder sb = getBaseGameUserKey(uid);
        sb.append(SPLIT);
        sb.append("0setting");
        return sb.toString();// getGameUserKey(uid) + SPLIT + "0setting";
    }

    @NonNull
    public static String getUserRoleInfoKey(Long uid) {
        StringBuilder sb = getBaseGameUserKey(uid);
        sb.append(SPLIT);
        sb.append("0roleInfo");
        return sb.toString();//getGameUserKey(uid) + SPLIT + "0roleInfo";
    }

    /**
     * 程序员运行时临时变量
     *
     * @param uid
     * @return
     */
    @NonNull
    public static String getRunTimeVarKey(Long uid, String businessKey) {
        StringBuilder sb = getBaseGameUserKey(uid);
        sb.append(SPLIT);
        sb.append("var");
        sb.append(SPLIT);
        sb.append(businessKey);
        return sb.toString();//getGameUserKey(uid) + SPLIT + "var" + SPLIT + businessKey;
    }

    /**
     * 获取的redis的基础key。格式：“玩家ID:数据类型”
     *
     * @param playerId
     * @param resType
     * @return
     */
    @NonNull
    public static String getDataTypeKey(Long playerId, UserDataType resType) {
        StringBuilder sb = getBaseGameUserKey(playerId);
        sb.append(SPLIT);
        sb.append(resType.getRedisKey());
//        String key = getGameUserKey(playerId) + SPLIT + resType.getRedisKey();
        return sb.toString();
    }

    /**
     * 获取PlayerResource的redis的基础key。格式：“玩家ID:数据类型”
     *
     * @param data
     * @return
     */
    @NonNull
    public static String getDataTypeKey(UserData data) {
        if (null == data.getGameUserId()) {
            String msg = data.getClass().getSimpleName() + "对象未设置区服玩家ID";
            CoderException.high(msg);
        }
        if (null == data.gainResType()) {
            String msg = data.getClass().getSimpleName() + "对象未设置数据类型";
            CoderException.high(msg);
        }
        return getDataTypeKey(data.getGameUserId(),data.gainResType());
//        String key = getGameUserKey(data.getGameUserId()) + SPLIT + data.gainResType().getRedisKey();
//        return key;
    }

    @NonNull
    public static String getUserDataKey(Long playerId, UserDataType dataType, Long dataId) {
        StringBuilder sb = getBaseGameUserKey(playerId);
        sb.append(SPLIT);
        sb.append(dataType.getRedisKey());
        sb.append(SPLIT);
        sb.append(dataId);
//        String key = getGameUserKey(playerId) + SPLIT + dataType.getRedisKey() + SPLIT + dataId;
        return sb.toString();
    }

    /**
     * 获取UserData的redis的key。格式：“玩家ID:数据类型:资源ID”
     *
     * @param data
     * @return
     */
    @NonNull
    public static String getUserDataKey(UserData data) {
        return getUserDataKey(data.getGameUserId(),data.gainResType(),data.getId());
//        String key = getDataTypeKey(data) + SPLIT + data.getId();
//        return key;
    }

    /**
     * 获取InsUserDataEntity的redis的key。格式：“玩家ID:数据类型:资源ID”
     *
     * @param entity
     * @return
     */
    @NonNull
    public static String getUserDataKey(InsUserDataEntity entity) {
        StringBuilder sb = getBaseGameUserKey(entity.getUid());
        sb.append(SPLIT);
        sb.append(entity.getDataType());
        sb.append(SPLIT);
        sb.append(entity.getDataId());
//        String key = getGameUserKey(entity.getUid()) + SPLIT + entity.getDataType() + SPLIT + entity.getDataId();
        return sb.toString();
    }

    /**
     * 创建一个资源ID
     *
     * @return
     */
    @NonNull
    public static Long getNewUserDataId() {
        return ID.INSTANCE.nextId();
    }


    @NonNull
    private static StringBuilder getBaseGameUserKey(Long uid) {
        StringBuilder sb = new StringBuilder();
        sb.append("usr");
        sb.append(SPLIT);
        sb.append(uid);
        return sb;
    }
}
