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
 * 全服高光时刻榜
 *
 * @author: suhq
 * @date: 2021/9/13 5:55 下午
 */
@Service
@Slf4j
public class TransmigrationHightLightTotalService {
    /** score分值：时间，member：uid@记录ID */
    @Autowired
    private RedisZSetUtil<String> rankingList;

    /**
     * 添加到全服高光
     *
     * @param transmigration
     * @param uid
     * @param recordId
     * @param score
     * @return 返回true，表示加入到全服高光
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
        String rankKey = getRankKey(transmigration);

        long size = rankingList.size(rankKey);
        //入榜条件
        if (size >= cfg.getHighLightNum()) {
            rankingList.removeRange(rankKey, 0L, 1L);
        }
        rankingList.add(rankKey, uid + "@" + recordId, DateUtil.toDateTimeLong());
        return true;
    }

    /**
     * 获得某个排行区间的记录。排行从1开始。
     *
     * @param transmigration
     * @param start
     * @param end
     * @return [uid@recordId,...]
     */
    public Set<String> getRankers(GameTransmigration transmigration, int start, int end) {
        String rankingKey = getRankKey(transmigration);
        return rankingList.reverseRange(rankingKey, start - 1, end - 1);
    }

    /**
     * 全服高光榜：game:transmigration:区服组:开始日期:rank:hightlight
     *
     * @param transmigration
     * @return
     */
    public static String getRankKey(GameTransmigration transmigration) {
        String key = TransmigrationKey.getBaseRankKey(transmigration);
        return key + RedisKeyConst.SPLIT + "highLight";
    }

}
