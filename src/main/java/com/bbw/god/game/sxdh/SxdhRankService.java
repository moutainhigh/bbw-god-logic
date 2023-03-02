package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.ZoneType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 神仙大会排行服务
 *
 * @author suhq
 * @date 2019-06-21 14:19:24
 */
@Service
@Slf4j
public class SxdhRankService {
    @Autowired
    private RedisZSetUtil<Long> rankingList;// score分值：玩家排行，member：玩家ID
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhDateService sxdhDateService;

    /**
     * 更新score,更新排行
     *
     * @param zone
     * @param rankType
     * @param uid
     * @param addValue
     */
    public void incrementRankValue(SxdhZone zone, SxdhRankType rankType, long uid, int addValue) {
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
    public int getRank(SxdhZone zone, SxdhRankType rankType, long uid) {
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
    public int getRank(SxdhRankType rankType, long uid) {
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        return getRank(sxdhZone, rankType, uid);
    }

    /**
     * 获得积分
     *
     * @param zone
     * @param rankType
     * @param uid
     * @return
     */
    public int getScore(SxdhZone zone, SxdhRankType rankType, long uid) {
        String key = getRankingKey(zone, rankType);
        Double score = rankingList.score(key, uid);
        return score.intValue();
    }

    /**
     * 根据排名获取积分
     *
     * @param zone
     * @param rankType
     * @param rank
     * @return
     */
    public int getScoreByRank(SxdhZone zone, SxdhRankType rankType, int rank) {
        String key = getRankingKey(zone, rankType);
        Set<TypedTuple<Long>> rankers = rankingList.reverseRangeWithScores(key, rank - 1, rank - 1);
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
    public Set<TypedTuple<Long>> getRankers(SxdhZone zone, SxdhRankType rankType, int start, int end) {
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
    public Set<Long> getRankersByScore(SxdhZone zone, SxdhRankType rankType, int minScore, int maxScore) {
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
    public Set<TypedTuple<Long>> getAllRankers(SxdhZone zone, SxdhRankType rankType) {
        String rankingKey = getRankingKey(zone, rankType);
        Set<TypedTuple<Long>> rankersSet = rankingList.reverseRangeWithScores(rankingKey);
        return rankersSet;
    }

    /**
     * 获得入榜人数
     *
     * @param zone
     * @param rankType
     * @return
     */
    public int getRankerNum(SxdhZone zone, SxdhRankType rankType) {
        String rankingKey = getRankingKey(zone, rankType);
        Long rankerNum = rankingList.size(rankingKey);
        return rankerNum.intValue();
    }

    public void removeRank(SxdhZone zone, SxdhRankType rankType) {
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
    private String getRankingKey(SxdhZone zone, SxdhRankType rankType) {
        String serverGroup = zone.getServerGroup().toString();
        ZoneType zoneType = ZoneType.fromValue(zone.getZone());
        String dateHourInt = zone.getSeason() + "";
        String zoneKeyPart = zoneType.getName() + dateHourInt;
        String date = "";
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        switch (rankType) {
            case PHASE_RANK:
                return GameRedisKey.getDataTypeKey(GameDataType.SXDH_ZONE, serverGroup, zoneKeyPart, "phaseRanking", seasonPhase.getId().toString());
            case LAST_PHASE_RANK:
                return GameRedisKey.getDataTypeKey(GameDataType.SXDH_ZONE, serverGroup, zoneKeyPart, "phaseRanking", seasonPhase.getPrePhase().toString());
            default:
                return GameRedisKey.getDataTypeKey(GameDataType.SXDH_ZONE, serverGroup, zoneKeyPart, "ranking");
        }
    }

    public void removeDayRank(SxdhZone zone, String date) {
        String rankingKey = getDayRankingKey(zone, date);
        if (rankingList.exists(rankingKey)) {
            rankingList.remove(rankingKey);
        }
    }

    public void incrementDayRankValue(SxdhZone zone, String date, long uid, int addValue) {
        String rankingKey = getDayRankingKey(zone, date);
        double delta = Double.valueOf(addValue);
        rankingList.incrementScore(rankingKey, uid, delta);
    }

    private String getDayRankingKey(SxdhZone zone, String date) {
        String serverGroup = zone.getServerGroup().toString();
        ZoneType zoneType = ZoneType.fromValue(zone.getZone());
        String dateHourInt = DateUtil.toHourInt(zone.getBeginDate()) + "";
        String zoneKeyPart = zoneType.getName() + dateHourInt;
        return GameRedisKey.getDataTypeKey(GameDataType.SXDH_ZONE, serverGroup, zoneKeyPart, "dayRanking", date);
    }
}
