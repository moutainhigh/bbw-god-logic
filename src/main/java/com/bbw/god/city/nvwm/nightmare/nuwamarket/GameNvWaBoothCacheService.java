package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.cache.LocalCache;
import com.bbw.common.DateUtil;
import com.bbw.god.game.award.Award;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 女娲集市摊位缓存服务
 *
 * @author fzj
 * @date 2022/6/9 17:35
 */
@Slf4j
@Service
public class GameNvWaBoothCacheService {
    private static final int CACHE_TIME = 600;
    @Autowired
    private NvWaMarketService nvWaMarketService;

    /**
     * 获取道具摊位
     *
     * @param treasureId
     * @return
     */
    public List<Long> getTreasureBooths(int treasureId) {
        return getData().getOrDefault(treasureId, new ArrayList<>());
    }

    /**
     * 获得缓存数据
     *
     * @return
     */
    public Map<Integer, List<Long>> getData() {
        String cacheKey = getCacheKey();
        if (!LocalCache.getInstance().containsKey(cacheKey)) {
            cacheData();
        }
        return LocalCache.getInstance().get(cacheKey);
    }

    /**
     * 缓存数据
     */
    public void cacheData() {
        String key = getCacheKey();
        log.info("更新女娲集市摊位本地缓存：" + key);
        List<GameNvWaBooth> allBooth = nvWaMarketService.getAllBooth();
        Map<Integer, List<Long>> cacheData = new HashMap<>();
        for (GameNvWaBooth booth : allBooth) {
            Date leaseEndTime = booth.getLeaseEndTime();
            if (booth.isExpired()) {
                continue;
            }
            List<Integer> goodsId = booth.getProductInfos().stream().map(p -> p.getGoods().getId()).distinct().collect(Collectors.toList());
            goodsId.forEach(g -> {
                List<Long> ids = cacheData.getOrDefault(g, new ArrayList<>());
                if (ids.isEmpty()) {
                    cacheData.put(g, ids);
                }
                ids.add(booth.getId());
            });
        }
        LocalCache.getInstance().put(key, cacheData, CACHE_TIME);
    }

    /**
     * 获得key
     *
     * @return
     */
    public String getCacheKey() {
        return "gameNvWaMarketBooth" + "_" + DateUtil.toDateTimeLong() / 100;
    }
}
