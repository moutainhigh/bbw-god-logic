package com.bbw.god.game.dfdj.rank;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.config.DfdjSegment;
import com.bbw.god.game.dfdj.config.ZoneType;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author suchaobin
 * @description 巅峰对决排行service
 * @date 2021/1/5 14:50
 **/
@Slf4j
@Service
public class DfdjRankService {
    @Autowired
    private RedisZSetUtil<Long> rankingList;// score分值：玩家排行，member：玩家ID
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjDateService dfdjDateService;

    /**
     * 更新score,更新排行
     *
     * @param zone
     * @param rankType
     * @param uid
     * @param addValue
     */
    public void incrementRankValue(DfdjZone zone, DfdjRankType rankType, long uid, int addValue) {
        String rankingKey = getRankingKey(zone, rankType);
        double delta = Double.valueOf(addValue);
        rankingList.incrementScore(rankingKey, uid, delta);
    }

    /**
     * 获得玩家排行
     *
     * @param uid
     * @return
     */
    public int getRank(DfdjZone zone, DfdjRankType rankType, long uid) {
        String key = getRankingKey(zone, rankType);
        Long myRank = rankingList.reverseRank(key, uid);
        if (myRank == null) {
            return 0;
        }
        return myRank.intValue() + 1;
    }

    /**
     * 获得玩家排行
     *
     * @param uid
     * @return
     */
    public int getRank(DfdjRankType rankType, long uid) {
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        return getRank(zone, rankType, uid);
    }

    /**
     * 获得积分
     *
     * @param zone
     * @param rankType
     * @param uid
     * @return
     */
    public int getScore(DfdjZone zone, DfdjRankType rankType, long uid) {
        String key = getRankingKey(zone, rankType);
        Double score = rankingList.score(key, uid);
        return score.intValue();
    }

    public DfdjSegment getSegment(DfdjZone zone, DfdjRankType rankType, long uid) {
        int score = getScore(zone, rankType, uid);
        if (score >= 180) {
            return DfdjSegment.SEVEN;
        }
        if (score >= 150) {
            return DfdjSegment.SIX;
        }
        if (score >= 120) {
            return DfdjSegment.FIVE;
        }
        if (score >= 90) {
            return DfdjSegment.FOUR;
        }
        if (score >= 60) {
            return DfdjSegment.THREE;
        }
        if (score >= 30) {
            return DfdjSegment.TWO;
        }
        return DfdjSegment.ONE;
    }

    /**
     * 根据排名获取积分
     *
     * @param zone
     * @param rankType
     * @param rank
     * @return
     */
    public int getScoreByRank(DfdjZone zone, DfdjRankType rankType, int rank) {
        String key = getRankingKey(zone, rankType);
        Set<ZSetOperations.TypedTuple<Long>> rankers = rankingList.reverseRangeWithScores(key, rank - 1, rank - 1);
        if (SetUtil.isNotEmpty(rankers)) {
            return rankers.stream().findFirst().get().getScore().intValue();
        }
        return 0;
    }

    /**
     * 获得某个排行区间的玩家。排行从1开始。
     *
     * @param zone
     * @param rankType
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getRankers(DfdjZone zone, DfdjRankType rankType, int start, int end) {
        String rankingKey = getRankingKey(zone, rankType);
        return rankingList.reverseRangeWithScores(rankingKey, start - 1, end - 1);
    }

    /**
     * 根据积分获取排名
     *
     * @param zone
     * @param rankType
     * @param minScore
     * @param maxScore
     * @return
     */
    public Set<Long> getRankersByScore(DfdjZone zone, DfdjRankType rankType, int minScore, int maxScore) {
        String rankingKey = getRankingKey(zone, rankType);
        return rankingList.reverseRangeByScore(rankingKey, minScore, maxScore);
    }

    /**
     * 获取整个榜单
     *
     * @param zone
     * @param rankType
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getAllRankers(DfdjZone zone, DfdjRankType rankType) {
        String rankingKey = getRankingKey(zone, rankType);
        Set<ZSetOperations.TypedTuple<Long>> rankersSet = rankingList.reverseRangeWithScores(rankingKey);
        return rankersSet;
    }

    /**
     * 获得入榜人数
     *
     * @param zone
     * @param rankType
     * @return
     */
    public int getRankerNum(DfdjZone zone, DfdjRankType rankType) {
        String rankingKey = getRankingKey(zone, rankType);
        Long rankerNum = rankingList.size(rankingKey);
        return rankerNum.intValue();
    }

    public void removeRank(DfdjZone zone, DfdjRankType rankType) {
        String rankingKey = getRankingKey(zone, rankType);
        if (rankingList.exists(rankingKey)) {
            rankingList.remove(rankingKey);
        }
    }

    /**
     * 获取排行的key
     *
     * @param zone
     * @param rankType
     * @return
     */
    private String getRankingKey(DfdjZone zone, DfdjRankType rankType) {
        String serverGroup = zone.getServerGroup().toString();
        ZoneType zoneType = ZoneType.fromValue(zone.getZone());
        String dateHourInt = zone.getSeason() + "";
        String zoneKeyPart = zoneType.getName() + dateHourInt;
        String date = "";
        CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getCurSeasonPhase();
        switch (rankType) {
            case PHASE_RANK:
                return GameRedisKey.getDataTypeKey(GameDataType.DFDJ_ZONE, serverGroup, zoneKeyPart, "phaseRanking", seasonPhase.getId().toString());
            case LAST_PHASE_RANK:
                return GameRedisKey.getDataTypeKey(GameDataType.DFDJ_ZONE, serverGroup, zoneKeyPart, "phaseRanking", seasonPhase.getPrePhase().toString());
            default:
                return GameRedisKey.getDataTypeKey(GameDataType.DFDJ_ZONE, serverGroup, zoneKeyPart, "ranking");
        }
    }

    public void removeDayRank(DfdjZone zone, String date) {
        String rankingKey = getDayRankingKey(zone, date);
        if (rankingList.exists(rankingKey)) {
            rankingList.remove(rankingKey);
        }
    }

    public void incrementDayRankValue(DfdjZone zone, String date, long uid, int addValue) {
        String rankingKey = getDayRankingKey(zone, date);
        double delta = Double.valueOf(addValue);
        rankingList.incrementScore(rankingKey, uid, delta);
    }

    private String getDayRankingKey(DfdjZone zone, String date) {
        String serverGroup = zone.getServerGroup().toString();
        ZoneType zoneType = ZoneType.fromValue(zone.getZone());
        String dateHourInt = DateUtil.toHourInt(zone.getBeginDate()) + "";
        String zoneKeyPart = zoneType.getName() + dateHourInt;
        return GameRedisKey.getDataTypeKey(GameDataType.DFDJ_ZONE, serverGroup, zoneKeyPart, "dayRanking", date);
    }
}
