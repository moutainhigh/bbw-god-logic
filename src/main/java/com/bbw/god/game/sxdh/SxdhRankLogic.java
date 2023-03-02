package com.bbw.god.game.sxdh;

import com.bbw.common.SetUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.award.RDRankerAward;
import com.bbw.god.game.sxdh.config.CfgSxdhRankAwardEntity;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.game.sxdh.config.ZoneType;
import com.bbw.god.game.sxdh.rd.RDSxdhLastSeasonRankerList;
import com.bbw.god.game.sxdh.rd.RDSxdhRankerAwardList;
import com.bbw.god.game.sxdh.rd.RDSxdhRankerList;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 神仙大会排行相关
 *
 * @author suhq
 * @date 2020-05-16 11:56
 **/
@Service
public class SxdhRankLogic {
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得排行
     *
     * @param type,见RankType
     * @return
     */
    public RDSxdhRankerList getFighterRank(long uid, int type) {
        SxdhRankType rankType = SxdhRankType.fromValue(type);
        SxdhZone sxdhZone = sxdhZoneService.getCurOrLastZone(uid);
        SxdhZone lastZone = sxdhZoneService.getLastZone(gameUserService.getOriServer(uid));
        // 新赛季第一天需要特出处理
        if (rankType == SxdhRankType.LAST_PHASE_RANK && sxdhZoneService.isLastZoneAsYesterday(lastZone)) {
            sxdhZone = lastZone;
        }

        List<RDSxdhRankerList.RDSxdhRanker> rdRankers = getRankers(sxdhZone, rankType, 1, SxdhTool.getSxdh().getNumToShow());
        RDSxdhRankerList rd = new RDSxdhRankerList();
        rd.setRankers(rdRankers);
        return rd;
    }

    public RDSxdhLastSeasonRankerList getLastSeasonFighterRank(long uid, int zone, int page, int limit) {
        RDSxdhLastSeasonRankerList rd = new RDSxdhLastSeasonRankerList();
        ZoneType zoneType = ZoneType.fromValue(zone);
        SxdhZone sxdhZone = null;
        CfgServerEntity server = gameUserService.getOriServer(uid);
        if (zoneType == null) {
            sxdhZone = sxdhZoneService.getLastZone(server);
        } else {
            sxdhZone = sxdhZoneService.getLastZone(server.getGroupId(), zone);
        }

        if (sxdhZone == null) {
            return new RDSxdhLastSeasonRankerList();
        }
        int maxRankNum = 100;
        if (zoneType == null) {
            int myRank = sxdhRankService.getRank(sxdhZone, SxdhRankType.RANK, uid);
            int myScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.RANK, uid);
            if (myRank > maxRankNum) {
                myRank = -1;
            }
            rd.setMyZone(sxdhZone.getZone());
            rd.setMyRank(myRank);
            rd.setMyScore(myScore);
        }
        int rankerNum = sxdhRankService.getRankerNum(sxdhZone, SxdhRankType.RANK);
        rankerNum = rankerNum > maxRankNum ? maxRankNum : rankerNum;
        rd.setTotalSize(rankerNum);
        int minRank = (page - 1) * limit + 1;
        int maxRank = page * limit;
        maxRank = maxRank > maxRankNum ? maxRankNum : maxRank;
        List<RDSxdhRankerList.RDSxdhRanker> rdRankers = getRankers(sxdhZone, SxdhRankType.RANK, minRank, maxRank);
        rd.setRankers(rdRankers);
        return rd;
    }

    public RDSxdhRankerAwardList getRankAward(int rankType) {
        List<CfgSxdhRankAwardEntity> rankAwardEntities = SxdhTool.getRankAwards(SxdhRankType.fromValue(rankType));
        List<RDRankerAward> rdAwards = new ArrayList<>();
        for (CfgSxdhRankAwardEntity rankAwardEntity : rankAwardEntities) {
            RDRankerAward rdAward = new RDRankerAward();
            rdAward.setMaxRank(rankAwardEntity.getMaxRank());
            rdAward.setMinRank(rankAwardEntity.getMinRank());
            rdAward.setAwards(RDAward.getInstances(rankAwardEntity.getAwards()));
            rdAwards.add(rdAward);
        }
        RDSxdhRankerAwardList rd = new RDSxdhRankerAwardList();
        rd.setRankerAwards(rdAwards);
        return rd;
    }

    private List<RDSxdhRankerList.RDSxdhRanker> getRankers(SxdhZone sxdhZone, SxdhRankType rankType, int minRank, int maxRank) {
        Set<ZSetOperations.TypedTuple<Long>> rankers = sxdhRankService.getRankers(sxdhZone, rankType, minRank, maxRank);
        List<RDSxdhRankerList.RDSxdhRanker> rdRankers = new ArrayList<>();
        if (SetUtil.isNotEmpty(rankers)) {
            int rank = minRank - 1;
            for (ZSetOperations.TypedTuple<Long> ranker : rankers) {
                rank++;
                long rankerId = ranker.getValue();
                int score = ranker.getScore().intValue();
                RDSxdhRankerList.RDSxdhRanker rdRanker = new RDSxdhRankerList.RDSxdhRanker();
                rdRanker.setId(rankerId);
                //积分排名
                rdRanker.setScore(score);
                rdRanker.setRank(rank);
                // 头像昵称
                GameUser gu = gameUserService.getGameUser(rankerId);
                rdRanker.setHead(gu.getRoleInfo().getHead());
                rdRanker.setNickname(gu.getRoleInfo().getNickname());
                rdRanker.setIconId(gu.getRoleInfo().getHeadIcon());
                // 区服短名称
                CfgServerEntity rankerServer = gameUserService.getOriServer(rankerId);
                rdRanker.setServer(rankerServer.getShortName());

                rdRankers.add(rdRanker);
            }
        }
        return rdRankers;
    }
}
