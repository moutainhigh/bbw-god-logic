package com.bbw.god.game.transmigration;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.cfg.CfgTransmigration;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 个人高光时刻榜
 *
 * @author: suhq
 * @date: 2021/9/13 5:55 下午
 */
@Service
@Slf4j
public class TransmigrationHightLightUserService {
    /** score分值：时间，member：记录ID */
    @Autowired
    private RedisZSetUtil<Long> rankingList;

    /**
     * 添加到个人高光
     *
     * @param transmigration
     * @param uid
     * @param recordId
     * @param score
     * @return 返回true，表示加入到个人高光
     */
    public boolean addToHightLight(GameTransmigration transmigration, long uid, long recordId, int score) {
        // 榜单不存在返回
        if (null == transmigration) {
            return false;
        }
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        //最小评分条件
        if (score < cfg.getHighLightMinScore()) {
            return false;
        }
        String rankKey = getRankKey(transmigration, uid);

        long size = rankingList.size(rankKey);
        //入榜条件
        if (size >= cfg.getHighLightNum()) {
            rankingList.removeRange(rankKey, 0L, 1L);
        }
        rankingList.add(rankKey, recordId, DateUtil.toDateTimeLong());
        return true;
    }

    /**
     * 获得某个排行区间的记录。排行从1开始。
     *
     * @param transmigration
     * @param start
     * @param end
     * @return
     */
    public Set<Long> getRankers(GameTransmigration transmigration, long uid, int start, int end) {
        String rankingKey = getRankKey(transmigration, uid);
        return rankingList.reverseRange(rankingKey, start - 1, end - 1);
    }

    /**
     * 玩家高光榜：game:transmigration:区服组:开始日期:rank:highlight:user:玩家ID
     *
     * @param transmigration
     * @param uid
     * @return
     */
    public static String getRankKey(GameTransmigration transmigration, long uid) {
        String key = TransmigrationKey.getBaseRankKey(transmigration);
        return key + RedisKeyConst.SPLIT + "highLight" + RedisKeyConst.SPLIT + "user" + RedisKeyConst.SPLIT + uid;
    }

}
