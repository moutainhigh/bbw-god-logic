package com.bbw.god.server.maou.alonemaou.attackinfo;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 魔王攻打明细记录
 * @date 2019-12-23 14:48
 **/
@Service
public class AloneMaouAttackSummaryService extends AbstractAttackDataService {
    @Autowired
    private RedisHashUtil<String, AloneMaouAttackSummary> attackSummaryRedisHashUtil;

    AloneMaouAttackSummaryService() {
        this.dataKey = "attackSummary";
    }

    /**
     * 设置我的攻打信息
     *
     * @param uid
     * @param maou
     * @param attackSummary
     */
    public void setMyAttackInfo(Long uid, ServerAloneMaou maou, AloneMaouAttackSummary attackSummary) {
        this.attackSummaryRedisHashUtil.putField(getRedisKey(maou), uid.toString(), attackSummary);
    }

    /**
     * 获得玩家打魔王的信息
     *
     * @param uid
     * @param maou
     * @return
     */
    public AloneMaouAttackSummary getMyAttackInfo(Long uid, ServerAloneMaou maou) {
        AloneMaouAttackSummary attackInfo = this.attackSummaryRedisHashUtil.getField(getRedisKey(maou), uid.toString());
        if (attackInfo == null) {
            attackInfo = AloneMaouAttackSummary.getInstance(uid, maou.getId());
            setMyAttackInfo(uid, maou, attackInfo);
        }
        return attackInfo;
    }

    /**
     * 获得所有的参与者
     *
     * @param maou
     * @return
     */
    public List<Long> getJoiners(ServerAloneMaou maou) {
        String key = getRedisKey(maou);
        return this.attackSummaryRedisHashUtil.get(key).keySet().stream().map(tmp -> Long.valueOf(tmp)).collect(Collectors.toList());
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String redisKey = getRedisKey(maou);
//        this.attackSummaryRedisHashUtil.expire(redisKey, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.attackSummaryRedisHashUtil.delete(redisKey);
    }
}
