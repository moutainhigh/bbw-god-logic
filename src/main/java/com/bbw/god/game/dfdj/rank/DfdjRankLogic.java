package com.bbw.god.game.dfdj.rank;

import com.bbw.common.SetUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.award.RDRankerAward;
import com.bbw.god.game.dfdj.config.CfgDfdjRankAwardEntity;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.config.DfdjTool;
import com.bbw.god.game.dfdj.config.ZoneType;
import com.bbw.god.game.dfdj.rd.RDDfdjLastSeasonRankerList;
import com.bbw.god.game.dfdj.rd.RDDfdjRankerAwardList;
import com.bbw.god.game.dfdj.rd.RDDfdjRankerList;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author suchaobin
 * @description 巅峰对决排行逻辑
 * @date 2021/1/5 14:50
 **/
@Service
public class DfdjRankLogic {
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得排行
     *
     * @param type,见RankType
     * @return
     */
    public RDDfdjRankerList getFighterRank(long uid, int type) {
        DfdjRankType rankType = DfdjRankType.fromValue(type);
        DfdjZone zone = dfdjZoneService.getCurOrLastZone(uid);
        DfdjZone lastZone = dfdjZoneService.getLastZone(gameUserService.getOriServer(uid));
        // 新赛季第一天需要特出处理
        if (rankType == DfdjRankType.LAST_PHASE_RANK && dfdjZoneService.isLastZoneAsYesterday(lastZone)) {
            zone = lastZone;
        }

        List<RDDfdjRankerList.RDDfdjRanker> rdRankers = getRankers(zone, rankType, 1, DfdjTool.getDfdj().getNumToShow());
        RDDfdjRankerList rd = new RDDfdjRankerList();
        rd.setRankers(rdRankers);
        return rd;
    }

    public RDDfdjLastSeasonRankerList getLastSeasonFighterRank(long uid, int zone, int page, int limit) {
        RDDfdjLastSeasonRankerList rd = new RDDfdjLastSeasonRankerList();
        ZoneType zoneType = ZoneType.fromValue(zone);
        DfdjZone dfdjZone = null;
        CfgServerEntity server = gameUserService.getOriServer(uid);
        if (zoneType == null) {
            dfdjZone = dfdjZoneService.getLastZone(server);
        } else {
            dfdjZone = dfdjZoneService.getLastZone(server.getGroupId(), zone);
        }

        if (dfdjZone == null) {
            return new RDDfdjLastSeasonRankerList();
        }
        int maxRankNum = 100;
        if (zoneType == null) {
            int myRank = dfdjRankService.getRank(dfdjZone, DfdjRankType.RANK, uid);
            int myScore = dfdjRankService.getScore(dfdjZone, DfdjRankType.RANK, uid);
            if (myRank > maxRankNum) {
                myRank = -1;
            }
            rd.setMyZone(dfdjZone.getZone());
            rd.setMyRank(myRank);
            rd.setMyScore(myScore);
        }
        int rankerNum = dfdjRankService.getRankerNum(dfdjZone, DfdjRankType.RANK);
        rankerNum = Math.min(rankerNum, maxRankNum);
        rd.setTotalSize(rankerNum);
        int minRank = (page - 1) * limit + 1;
        int maxRank = page * limit;
        maxRank = Math.min(maxRank, maxRankNum);
        List<RDDfdjRankerList.RDDfdjRanker> rdRankers = getRankers(dfdjZone, DfdjRankType.RANK, minRank, maxRank);
        rd.setRankers(rdRankers);
        return rd;
    }

    public RDDfdjRankerAwardList getRankAward(int rankType) {
        List<CfgDfdjRankAwardEntity> rankAwardEntities = DfdjTool.getRankAwards(DfdjRankType.fromValue(rankType));
        List<RDRankerAward> rdAwards = new ArrayList<>();
        for (CfgDfdjRankAwardEntity rankAwardEntity : rankAwardEntities) {
            RDRankerAward rdAward = new RDRankerAward();
            rdAward.setMaxRank(rankAwardEntity.getMaxRank());
            rdAward.setMinRank(rankAwardEntity.getMinRank());
            rdAward.setAwards(RDAward.getInstances(rankAwardEntity.getAwards()));
            rdAwards.add(rdAward);
        }
        RDDfdjRankerAwardList rd = new RDDfdjRankerAwardList();
        rd.setRankerAwards(rdAwards);
        return rd;
    }

    private List<RDDfdjRankerList.RDDfdjRanker> getRankers(DfdjZone dfdjZone, DfdjRankType rankType, int minRank, int maxRank) {
        Set<ZSetOperations.TypedTuple<Long>> rankers = dfdjRankService.getRankers(dfdjZone, rankType, minRank, maxRank);
        List<RDDfdjRankerList.RDDfdjRanker> rdRankers = new ArrayList<>();
        if (SetUtil.isNotEmpty(rankers)) {
            int rank = minRank - 1;
            for (ZSetOperations.TypedTuple<Long> ranker : rankers) {
                rank++;
                long rankerId = ranker.getValue();
                int score = ranker.getScore().intValue();
                RDDfdjRankerList.RDDfdjRanker rdRanker = new RDDfdjRankerList.RDDfdjRanker();
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
