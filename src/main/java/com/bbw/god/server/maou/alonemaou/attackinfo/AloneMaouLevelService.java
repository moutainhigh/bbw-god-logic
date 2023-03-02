package com.bbw.god.server.maou.alonemaou.attackinfo;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 独战魔王层级服务类
 *
 * @author suhq
 * @date 2019年12月20日 下午5:19:50
 */
@Slf4j
@Service
public class AloneMaouLevelService extends AbstractAttackDataService {
    @Autowired
    private RedisHashUtil<String, AloneMaouLevelInfo> maouLevelRedis;// 魔王层级信息
    @Autowired
    private AloneMaouAttackSummaryService attackSummaryService;

    AloneMaouLevelService() {
        this.dataKey = "maouLevelInfo";
    }

    /**
     * 保存层级记录
     *
     * @param maou
     * @param levelInfo
     */
    public void saveMaouLevelInfo(ServerAloneMaou maou, AloneMaouLevelInfo levelInfo) {
        String maouAttackedKey = getMaouLevelKey(levelInfo.getGuId(), maou);
        this.maouLevelRedis.putField(maouAttackedKey, levelInfo.getMaouLevel() + "", levelInfo);
    }

    /**
     * 获取层级记录
     *
     * @param uid
     * @param maou
     * @return
     */
    public Map<String, AloneMaouLevelInfo> getMaouLevelInfo(long uid, ServerAloneMaou maou) {
        String maouLevelKey = getMaouLevelKey(uid, maou);
        return this.maouLevelRedis.get(maouLevelKey);
    }

    /**
     * 清除魔王记录
     *
     * @param uid
     * @param maou
     */
    public void removeMaouLevelInfo(long uid, ServerAloneMaou maou) {
        String maouLevelKey = getMaouLevelKey(uid, maou);
        if (this.maouLevelRedis.exists(maouLevelKey)) {
            this.maouLevelRedis.delete(maouLevelKey);
        }
    }

    /**
     * 将独战魔王层级信息置为过期
     *
     * @param maou
     */
    @Override
    public void expireData(BaseServerMaou maou) {
        ServerAloneMaou aloneMaou = (ServerAloneMaou) maou;
        List<Long> joiners = this.attackSummaryService.getJoiners(aloneMaou);
        joiners.stream().forEach(tmp -> {
            String maouAttackedKey = getMaouLevelKey(tmp, aloneMaou);
//            this.maouLevelRedis.expire(maouAttackedKey, TIME_OUT_DAYS, TimeUnit.DAYS);
            this.maouLevelRedis.delete(maouAttackedKey);
        });
    }

    /**
     * 魔王层级数据key
     *
     * @param maou
     * @return
     */
    private String getMaouLevelKey(long uid, ServerAloneMaou maou) {
        String baseKey = getRedisKey(maou);
        String levelKey = baseKey + ServerRedisKey.SPLIT + uid;
        return levelKey;
    }
}
