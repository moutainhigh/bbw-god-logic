package com.bbw.god.gm;

import com.bbw.cache.GameCacheService;
import com.bbw.cache.ServerCacheService;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.zxz.entity.ZxzInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 缓存数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@Service
public class CacheGMService {
    @Autowired
    private ServerCacheService serverCacheService;
    @Autowired
    private GameCacheService gameCacheService;

    public void reloadServerCache(int sId) {
        this.serverCacheService.cacheData(sId, ServerActivity.class);
        this.serverCacheService.cacheData(sId, ServerActivityRank.class);
    }

    public void reloadGameCache() {
        this.gameCacheService.cacheData(GameActivity.class);
        this.gameCacheService.cacheData(GameActivityRank.class);
        this.gameCacheService.cacheData(GameTransmigration.class);
        this.gameCacheService.cacheData(ZxzInfo.class);
        this.gameCacheService.cacheData(ZxzFourSaintsInfo.class);
        
    }


}
