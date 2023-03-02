package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.db.redis.RedisValueUtil;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author suhq
 * @description: 魔王损血
 * @date 2019-12-23 14:48
 **/
@Service
public class BossMaouBloodService extends AbstractAttackDataService {
    @Autowired
    private RedisValueUtil<Integer> lostBloodRedisValue;// 魔王失血量

    BossMaouBloodService() {
        this.dataKey = "lostBlood";
    }

    /**
     * 获得失血量
     *
     * @param maou
     * @return
     */
    public Integer getLostBlood(ServerBossMaou maou) {
        String lostBloodKey = getRedisKey(maou);
        Integer lostBlood = this.lostBloodRedisValue.get(lostBloodKey);
        return Optional.ofNullable(lostBlood).orElse(0);
    }

    /**
     * 增加失血量
     *
     * @param maou
     * @param beatedBlood
     * @return 返回加后的值
     */
    public int incBlood(ServerBossMaou maou, int beatedBlood) {
        String lostBloodKey = getRedisKey(maou);
        Long lostBlood = this.lostBloodRedisValue.increment(lostBloodKey, beatedBlood);
        return lostBlood.intValue();
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String redisKey = getRedisKey(maou);
//        this.lostBloodRedisValue.expire(redisKey, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.lostBloodRedisValue.delete(redisKey);

    }
}
