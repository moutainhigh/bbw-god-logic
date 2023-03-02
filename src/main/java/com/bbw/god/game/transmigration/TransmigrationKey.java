package com.bbw.god.game.transmigration;

import com.bbw.common.DateUtil;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.entity.GameTransmigration;

/**
 * 轮回key
 *
 * @author: suhq
 * @date: 2021/9/16 10:17 上午
 */
public class TransmigrationKey {
    /**
     * 轮回世界基本key：game:transmigration:区服组:开始日期
     *
     * @param transmigration
     * @return
     */
    public static String getBaseKey(GameTransmigration transmigration) {
        String serverGroup = transmigration.getSgId().toString();
        String dateInt = DateUtil.toDateInt(transmigration.getBegin()) + "";
        return GameRedisKey.getDataTypeKey(GameDataType.TRANSMIGRATION, serverGroup, dateInt);
    }

    /**
     * 轮回世界基本排行key：game:transmigration:区服组:开始日期:rank
     *
     * @param transmigration
     * @return
     */
    public static String getBaseRankKey(GameTransmigration transmigration) {
        String baseKey = getBaseKey(transmigration);
        return baseKey + RedisKeyConst.SPLIT + "rank";
    }
}
