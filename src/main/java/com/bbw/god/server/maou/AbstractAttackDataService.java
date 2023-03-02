package com.bbw.god.server.maou;

import com.bbw.god.server.redis.ServerRedisKey;

/**
 * @author suhq
 * @description: 攻打信息服务
 * @date 2019-12-23 14:56
 **/
public abstract class AbstractAttackDataService {
    protected static final long TIME_OUT_DAYS = 10;//10天
    protected String dataKey;

    public abstract void expireData(BaseServerMaou maou);

    protected String getRedisKey(BaseServerMaou maou) {
        String baseKey = ServerRedisKey.getServerDataKey(maou);
        String key = baseKey + ServerRedisKey.SPLIT + this.dataKey;
        return key;
    }
}
