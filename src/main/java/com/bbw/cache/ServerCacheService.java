package com.bbw.cache;

import com.bbw.common.DateUtil;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.ServerDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suhq
 * @description: 区服数据本地缓存接口
 * @date 2020-02-24 09:54
 **/
@Slf4j
@Service
public class ServerCacheService {
    private static String CACHE_TYPE = "serverDataCache";
    private static int CACHE_TIME = DateUtil.SECOND_ONE_DAY * 2;
    @Autowired
    private ServerDataService serverDataService;

    /**
     * 获得区服所有特定类型数据(基于本地缓存)
     *
     * @param sId
     * @return
     */
    public <T extends ServerData> List<T> getServerDatas(int sId, Class<T> clazz) {
        String cacheKey = getCacheKey(sId, clazz);
        if (!LocalCache.getInstance().containsKey(CACHE_TYPE, cacheKey)) {
            cacheData(sId, clazz);
        }
        return LocalCache.getInstance().get(CACHE_TYPE, cacheKey);
    }

    public <T extends ServerData> List<T> getServerDatas(int sId, Class<T> clazz, String... loop) {
        String cacheKey = getCacheKey(sId, clazz);
        if (!LocalCache.getInstance().containsKey(CACHE_TYPE, cacheKey)) {
            cacheData(sId, clazz, loop);
        }
        return LocalCache.getInstance().get(CACHE_TYPE, cacheKey);
    }

    /**
     * 缓存数据
     *
     * @param sId
     * @param clazz
     * @param <T>
     */
    public <T extends ServerData> void cacheData(int sId, Class<T> clazz) {
        String key = getCacheKey(sId, clazz);
        System.out.println("更新区服数据本地缓存：" + key);
        log.info("更新区服数据本地缓存：" + key);
        List<T> sds = this.serverDataService.getServerDatas(sId, clazz);
        LocalCache.getInstance().put(CACHE_TYPE, key, sds, CACHE_TIME);
    }

    public <T extends ServerData> void cacheData(int sId, Class<T> clazz, String... loop) {
        String key = getCacheKey(sId, clazz);
        System.out.println("更新区服数据本地缓存：" + key);
        log.info("更新区服数据本地缓存：" + key);
        List<T> sds = this.serverDataService.getServerDatas(sId, clazz, loop);
        LocalCache.getInstance().put(CACHE_TYPE, key, sds, CACHE_TIME);
    }

    /**
     * 获得缓存key
     *
     * @param sId
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends ServerData> String getCacheKey(int sId, Class<T> clazz) {
        ServerDataType serverDataType = ServerDataType.fromClass(clazz);
        String key = "local_server_" + sId + "_" + serverDataType.getRedisKey() + "_" + DateUtil.getTodayInt();
        return key;
    }
}
