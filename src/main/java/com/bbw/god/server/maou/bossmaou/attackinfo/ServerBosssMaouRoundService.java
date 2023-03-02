package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.cache.LocalCache;
import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.BossMaouTool;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author suhq
 * @description: 魔王回合服务
 * @date 2019-12-23 16:16
 **/
@Slf4j
@Service
public class ServerBosssMaouRoundService extends AbstractAttackDataService {

    @Autowired
    private RedisHashUtil<String, BossMaouRoundDetail> roundRedisHash;// 魔王回合

    ServerBosssMaouRoundService() {
        this.dataKey = "maouRound";
    }

    /**
     * 初始化魔王回合数据
     *
     * @param maou
     */
    public void initRoundData(ServerBossMaou maou) {
        String key = getRedisKey(maou);
        this.roundRedisHash.delete(key);
        log.info("初始化魔王回合数据。" + maou.toString());
        CfgBossMaou.BossMaou bossMaouConfig = BossMaouTool.getBossMaouConfig(maou.getBaseMaouId());
        Integer maouLevel = bossMaouConfig.getMaouLevel();
        // 混沌魔灵没有属性
        int nextType = maouLevel == 10 ? 0 : maou.getType();
        for (int round = 1; round <= bossMaouConfig.getTotalRound(); round++) {
            BossMaouRoundDetail roundDetail = new BossMaouRoundDetail();
            roundDetail.setMaouId(maou.getId());
            roundDetail.setRound(round);
            roundDetail.setType(nextType);
            Date roundBegin = DateUtil.addSeconds(maou.getAttackTime(), bossMaouConfig.getTimePerRound() * (round - 1));
            roundDetail.setRoundBegin(roundBegin);
            Date roundEnd = DateUtil.addSeconds(roundBegin, bossMaouConfig.getTimePerRound());
            roundDetail.setRoundEnd(roundEnd);
            if (round % bossMaouConfig.getRoudToChangeType() == 0) {
                nextType = getNextType(nextType);
            }
            addRoundDetail(maou, roundDetail);
        }
    }

    /**
     * 获得当前回合魔王数据
     *
     * @param maou
     * @return
     */
    public BossMaouRoundDetail getCurRoundDetail(ServerBossMaou maou) {
        Map<String, BossMaouRoundDetail> roundDetailMap = getRoundDetails(maou);
        long roundTime = maou.gainRoundTime();
        String round = roundDetailMap.keySet().stream().filter(tmp -> roundDetailMap.get(tmp).ifMatch(roundTime)).findFirst().orElse(null);
        BossMaouRoundDetail roundDetail = roundDetailMap.get(round);
        return roundDetail;
    }

    /**
     * 获取魔王回合数据
     *
     * @param maou
     * @return
     */
    private Map<String, BossMaouRoundDetail> getRoundDetails(ServerBossMaou maou) {
        String key = getRedisKey(maou);
        if (!this.roundRedisHash.exists(key)) {
            log.error("初始化魔王回合信息，正常不应该执行到此步");
            initRoundData(maou);
        }
        //如果本地缓存有数据，则从本次缓存读取
        if (LocalCache.getInstance().containsKey(key)) {
            return LocalCache.getInstance().get(key);
        }
        //从Redis读数据
        Map<String, BossMaouRoundDetail> roundDetailMap = this.roundRedisHash.get(key);
        //将魔王回合信息缓存在本地
        LocalCache.getInstance().put(key, roundDetailMap, LocalCache.ONE_DAY);
        return roundDetailMap;
    }

    /**
     * 添加魔王回合明细数据
     *
     * @param maou
     * @param roundDetail
     */
    public void addRoundDetail(ServerBossMaou maou, BossMaouRoundDetail roundDetail) {
        String key = getRedisKey(maou);
        this.roundRedisHash.putField(key, roundDetail.getRound().toString(), roundDetail);
    }

    private int getNextType(int type) {
        if (0 == type) {
            return 0;
        }
        int nextType = type + 10;
        if (nextType > 50) {
            nextType = 10;
        }
        return nextType;
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String key = getRedisKey(maou);
//        this.roundRedisHash.expire(key, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.roundRedisHash.delete(key);
    }
}
