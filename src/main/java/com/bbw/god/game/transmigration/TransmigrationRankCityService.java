package com.bbw.god.game.transmigration;

import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.data.redis.RedisKeyConst;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 轮回世界单个城市挑战排行
 *
 * @author: suhq
 * @date: 2021/9/13 3:29 下午
 */
@Service
@Slf4j
public class TransmigrationRankCityService {
    /** score分值：评分，member：玩家ID **/
    @Autowired
    private RedisZSetUtil<Long> rankingList;

    @Autowired
    private TransmigrationCityRecordService transmigrationCityRecordService;
    @Autowired
    private TransmigrationCityNewRecordTimeService cityNewRecordTimeService;

    /**
     * 更新新的记录
     *
     * @param transmigration
     * @param uid
     * @param cityId
     * @param recordId
     * @param score
     * @return false 非新纪录 true 新的记录
     */
    public boolean updateNewRecord(GameTransmigration transmigration, long uid, int cityId, long recordId, int score) {
        // 榜单不存在返回
        if (null == transmigration) {
            return false;
        }
        String rankKey = getRankKey(transmigration, cityId);

        //只有新纪录才会做相关数据更新处理
        Long bestRecordId = transmigrationCityRecordService.getBestRecordId(transmigration, uid, cityId);
        if (bestRecordId > 0) {
            int bestScore = getScore(transmigration, cityId, uid);
            if (score <= bestScore) {
                return false;
            }
        }
        bestRecordId = recordId;
        //更新最好记录
        transmigrationCityRecordService.updateBestRecordId(transmigration, uid, cityId, bestRecordId);
        //更新榜单
        rankingList.add(rankKey, uid, score);
        //新纪录的时间
        cityNewRecordTimeService.updateNewRecordTime(transmigration, uid);
        return true;
    }

    /**
     * 获得评分
     *
     * @param transmigration
     * @param cityId
     * @param uid
     * @return
     */
    public int getScore(GameTransmigration transmigration, int cityId, long uid) {
        String key = getRankKey(transmigration, cityId);
        Double score = rankingList.score(key, uid);
        return score.intValue();
    }

    /**
     * 获取记录排行
     *
     * @param transmigration
     * @param cityId
     * @return
     */
    public int getScoreAsNo1(GameTransmigration transmigration, int cityId) {
        String key = getRankKey(transmigration, cityId);

        Set<TypedTuple<Long>> bestRecords = rankingList.reverseRangeWithScores(key, 0, 1);
        if (SetUtil.isEmpty(bestRecords)) {
            return 0;
        }
        Double score = bestRecords.iterator().next().getScore();
        return score.intValue();
    }

    /**
     * 获取最好的记录(多个第一名)
     *
     * @param transmigration
     * @param cityId
     * @return
     */
    public List<Long> getBestUids(GameTransmigration transmigration, int cityId) {
        String key = getRankKey(transmigration, cityId);
        int score = getScoreAsNo1(transmigration, cityId);
        if (score == 0) {
            return new ArrayList<>();
        }
        Set<Long> result = rankingList.rangeByScore(key, score, score);
        return result.stream().collect(Collectors.toList());
    }

    /**
     * 获得高排名的玩家
     *
     * @param transmigration
     * @param cityId
     * @param limit
     * @return
     */
    public List<Long> getTopUids(GameTransmigration transmigration, int cityId, int limit) {
        String key = getRankKey(transmigration, cityId);
        Set<TypedTuple<Long>> bestRecords = rankingList.reverseRangeWithScores(key, 0, limit - 1);
        if (SetUtil.isEmpty(bestRecords)) {
            return new ArrayList<>();
        }
        return bestRecords.stream().map(tmp -> tmp.getValue()).collect(Collectors.toList());
    }

    /**
     * 城池挑战榜：game:transmigration:区服组:开始日期:rank:city:城池ID
     *
     * @param transmigration
     * @param cityId
     * @return
     */
    public static String getRankKey(GameTransmigration transmigration, int cityId) {
        String key = TransmigrationKey.getBaseRankKey(transmigration);
        return key + RedisKeyConst.SPLIT + "city" + RedisKeyConst.SPLIT + cityId;
    }

}
