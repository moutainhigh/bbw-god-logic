package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 更新本地缓存
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMLocalCacheCtrl extends AbstractController {
    @Autowired
    private CacheGMService cacheGMService;

    @RequestMapping("localCache!refreshCfgFromDb")
    public Rst reloadServer() {
        Cfg.I.reloadWithoutClear(CfgServerEntity.class);
        Cfg.I.reloadWithoutClear(CfgChannelEntity.class);
        Cfg.I.reloadWithoutClear(CfgActivityEntity.class);
        Cfg.I.reloadWithoutClear(CfgActivityRankEntity.class);
        return Rst.businessOK();
    }

    @RequestMapping("localCache!refreshCfgFromYml")
    public Rst reloadCfgFromYml() {
        Cfg.I.reloadWithoutClear(CfgCardEntity.class);
        Cfg.I.reloadWithoutClear(CfgTreasureEntity.class);
        Cfg.I.reloadWithoutClear(CfgSpecialEntity.class);
        return Rst.businessOK();
    }

    /**
     * 更新本地区服缓存，
     * 如果serverNames==所有，则更新所有区服缓存和全服缓存
     * 如果serverNames!=所有，更新指定区服的缓存
     *
     * @return
     */
    @RequestMapping("localCache!refreshServerAndGameCache")
    public Rst reloadServerCache() {
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        servers.forEach(tmp -> {
            cacheGMService.reloadServerCache(tmp.getMergeSid());
        });
        cacheGMService.reloadGameCache();
        return Rst.businessOK();
    }
}
