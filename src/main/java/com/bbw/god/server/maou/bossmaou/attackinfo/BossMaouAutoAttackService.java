package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suhq
 * @description: 魔王自动战斗记录
 * @date 2019-12-23 14:48
 **/
@Service
public class BossMaouAutoAttackService extends AbstractAttackDataService {
    @Autowired
    private RedisSetUtil<Long> autoAttackUidRedisSet;// 自动战斗玩家集

    BossMaouAutoAttackService() {
        this.dataKey = "autoAttackUids";
    }

    /**
     * 自动战斗
     *
     * @param maou
     * @param uid
     */
    public void addAuto(ServerBossMaou maou, long uid) {
        String redisKey = getRedisKey(maou);
        this.autoAttackUidRedisSet.add(redisKey, uid);
    }

    /**
     * 取消自动
     *
     * @param maou
     * @param uid
     */
    public void cancelAuto(ServerBossMaou maou, Long uid) {
        String redisKey = getRedisKey(maou);
        this.autoAttackUidRedisSet.remove(redisKey, uid);
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String redisKey = getRedisKey(maou);
//        this.autoAttackUidRedisSet.expire(redisKey, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.autoAttackUidRedisSet.delete(redisKey);
    }
}
