package com.bbw.god.server.redis;

import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.InsServerDataEntity;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import org.springframework.lang.NonNull;

/**
 * 区服的redis键值
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-14 21:33
 */
public class ServerRedisKey extends RedisKeyConst {
    public static final String PREFIX = "server";

    /**
     * 程序员运行时临时变量
     *
     * @param serverId
     * @param businessKey
     * @return
     */
    @NonNull
    public static String getRunTimeVarKey(int serverId, String businessKey) {
        StringBuilder sb = getServerBaseKey(serverId);
        sb.append(SPLIT);
        sb.append("var");
        sb.append(SPLIT);
        sb.append(businessKey);
        return sb.toString();//getServerBaseKey(serverId) + SPLIT + "var" + SPLIT + businessKey;
    }

    /**
     * 获取的redis的基础key。格式：“区服前缀键值:数据类型:具体业务”
     *
     * @param dataType
     * @param business
     * @return
     */
    @NonNull
    public static String getDataTypeKey(int serverId, ServerDataType dataType, String... business) {
        StringBuilder sb = getServerBaseKey(serverId);
        sb.append(SPLIT);
        sb.append(dataType.getRedisKey());
//        String key = getServerBaseKey(serverId) + SPLIT + dataType.getRedisKey();
        if (0 == business.length) {
            return sb.toString();
        }
        for (String s : business) {
            sb.append(SPLIT);
            sb.append(s);
//            key += SPLIT + s;
        }
        return sb.toString();
    }

    @NonNull
    public static String getDataTypeKey(ServerData data) {
        String key = getDataTypeKey(data.getSid(), data.gainDataType());
        if (data.isLoopData() && StrUtil.isNotNull(data.getLoopKey())) {
            key = getDataTypeKey(data.getSid(), data.gainDataType(), data.getLoopKey());
        }
        return key;
    }

    /**
     * 获取ServerData的redis的key。格式：“区服前缀键值:数据类型:资源ID”
     *
     * @param data
     * @return
     */
    @NonNull
    public static String getServerDataKey(ServerData data) {
        StringBuilder sb = new StringBuilder();
        sb.append(getDataTypeKey(data.getSid(), data.gainDataType()));
        sb.append(SPLIT);
        sb.append(data.getId());
//        String key =  getDataTypeKey(data.getSid(), data.gainDataType()) + SPLIT + data.getId();
        return sb.toString();
    }

    /**
     * 获取ServerData的redis的key。格式：“区服前缀键值:数据类型:资源ID”
     *
     * @param serverId
     * @param resType
     * @param dataId
     * @return
     */
    @NonNull
    public static String getServerDataKey(int serverId, ServerDataType resType, Long dataId) {
        StringBuilder sb = getServerBaseKey(serverId);
        sb.append(SPLIT);
        sb.append(resType.getRedisKey());
        sb.append(SPLIT);
        sb.append(dataId);
//        String key = getServerBaseKey(serverId) + SPLIT + resType.getRedisKey() + SPLIT + dataId;
        return sb.toString();
    }

    /**
     * 获取ServerDataEntity的redis的key。格式：“区服前缀键值:数据类型:资源ID”
     *
     * @param data
     * @return
     */
    @NonNull
    public static String getServerDataKey(InsServerDataEntity data) {
        StringBuilder sb = getServerBaseKey(data.getSid());
        sb.append(SPLIT);
        sb.append(data.getDataType());
        sb.append(SPLIT);
        sb.append(data.getDataId());
//        String key = getServerBaseKey(data.getSid()) + SPLIT + data.getDataType() + SPLIT + data.getDataId();
        return sb.toString();
    }

    /**
     * 获取区服redis前缀键值
     *
     * @param serverId
     * @return
     */
    @NonNull
    public static StringBuilder getServerBaseKey(int serverId) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(SPLIT);
        sb.append(serverId);
//        String key = PREFIX + SPLIT + serverId;
        return sb;
    }
}
