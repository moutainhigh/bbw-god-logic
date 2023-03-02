package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suhq
 * @description: 魔王攻打明细记录
 * @date 2019-12-23 14:48
 **/
@Service
public class BossMaouAttackDetailService extends AbstractAttackDataService {
    @Autowired
    private RedisSetUtil<BossMaouAttackDetail> attackDetailRedisSet;// 魔王攻打明细记录

    BossMaouAttackDetailService() {
        this.dataKey = "attackDetail";
    }

    /**
     * 保存攻击明细
     *
     * @param maou
     * @param attackDetail
     */
    public void saveAttackDetail(ServerBossMaou maou, BossMaouAttackDetail attackDetail) {
        String redisKey = getRedisKey(maou);
        this.attackDetailRedisSet.add(redisKey, attackDetail);
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String redisKey = getRedisKey(maou);
//        this.attackDetailRedisSet.expire(redisKey, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.attackDetailRedisSet.delete(redisKey);
    }
}
