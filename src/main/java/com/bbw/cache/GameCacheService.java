package com.bbw.cache;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.data.GameDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 全服数据本地缓存接口
 *
 * @author suhq
 * @date 2020-02-24 09:54
 **/
@Slf4j
@Service
public class GameCacheService {
    private static int CACHE_TIME = DateUtil.SECOND_ONE_DAY * 2;
    @Autowired
    private GameDataService gameDataService;

    /**
     * 获取数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends GameData> List<T> getGameDatas(Class<T> clazz) {
        String cacheKey = getCacheKey(clazz);
        if (!LocalCache.getInstance().containsKey(cacheKey)) {
            cacheData(clazz);
        }
        return LocalCache.getInstance().get(cacheKey);
    }

    /**
     * 添加数据
     *
     * @param datas
     * @param <T>
     */
    public <T extends GameData> void addGameDatas(List<T> datas) {
        if (ListUtil.isEmpty(datas)) {
            return;
        }
        this.gameDataService.addGameDatas(datas);
        //更新缓存
        Class<T> clazz = (Class<T>) datas.get(0).getClass();
        cacheData(clazz);
    }

    /**
     * 缓存数据
     *
     * @param clazz
     * @param <T>
     */
    public <T extends GameData> void cacheData(Class<T> clazz) {
        String key = getCacheKey(clazz);
        log.info("更新全服本地缓存：" + key);
        List<T> gas = this.gameDataService.getGameDatas(clazz);
        LocalCache.getInstance().put(key, gas, CACHE_TIME);
    }

    public <T extends GameData> String getCacheKey(Class<T> clazz) {
        GameDataType gameDataType = GameDataType.fromClass(clazz);
        String key = "local_game_" + gameDataType.getRedisKey() + "_" + DateUtil.getTodayInt();
        return key;
    }
}
