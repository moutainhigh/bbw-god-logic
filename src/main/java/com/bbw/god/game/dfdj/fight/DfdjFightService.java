package com.bbw.god.game.dfdj.fight;

import com.bbw.common.DateUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.dfdj.DfdjDateService;
import com.bbw.god.game.dfdj.DfdjMedicineService;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.DfdjRankType;
import com.bbw.god.game.dfdj.config.DfdjTool;
import com.bbw.god.game.dfdj.rank.DfdjRankService;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 巅峰对决战斗服务
 * @date 2021/1/6 08:51
 **/
@Slf4j
@Service
public class DfdjFightService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DfdjDateService dfdjDateService;
    @Autowired
    private DfdjFighterService dfdjFighterService;
    @Autowired
    private DfdjMedicineService dfdjMedicineService;
    @Autowired
    private DfdjRankService dfdjRankService;
    @Autowired
    private UserTreasureService userTreasureService;

    public int[] handleAsDfdjWin(DfdjZone zone, long winner, long loser, int roomId) {
        int[] res = {0, 0};//总得分，额外分数
        // 赛季结束
        if (null == zone) {
            return res;
        }
        DfdjFighter fighter = dfdjFighterService.getFighter(winner);
        DfdjFightCache dfdjFightCache = TimeLimitCacheUtil.getDfdjFightCache(winner);
        int winnerScore = dfdjFightCache.getScore(winner);
        int winnerSegment = DfdjTool.getSegmentByScore(winnerScore).getId();
        int winnerRank = dfdjFightCache.getRank(winner);
        int loserScore = dfdjFightCache.getScore(loser);
        int loserSegment = DfdjTool.getSegmentByScore(loserScore).getId();
        int loserRank = dfdjFightCache.getRank(loser);
        //段位加分
        int segmentScore = 0;
        CfgDfdj.ScoreAward segmentScoreAward = DfdjTool.getSegmentScoreAward(winnerSegment, loserSegment - winnerSegment);
        if (segmentScoreAward != null) {
            segmentScore = segmentScoreAward.getWinScore();
        }
        //排名加分
        int rankScore = 0;
        CfgDfdj.ScoreAward rankScoreAward = DfdjTool.getRankScoreAward(winnerRank - loserRank);
        if (rankScoreAward != null) {
            rankScore = rankScoreAward.getWinScore();
        }

        int baseScore = rankScore + segmentScore;
        CfgDfdj.SeasonPhase seasonPhase = dfdjDateService.getCurSeasonPhase();
        if (seasonPhase != null) {
            baseScore *= seasonPhase.getDoubles();
        }
        int addedScore = baseScore;

        boolean deled = userTreasureService.delTreasure(winner, TreasureEnum.YuanQD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        if (deled) {
            addedScore *= 2;// 元气丹分翻倍
        }
        // 处理排行
        dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, winner, addedScore);
        dfdjRankService.incrementRankValue(zone, DfdjRankType.PHASE_RANK, winner, addedScore);

        // 设置连胜
        if (fighter.getStreak() < 0) {
            fighter.setStreak(1);
        } else {
            fighter.setStreak(fighter.getStreak() + 1);
            if (fighter.getMaxStreak() < fighter.getStreak()) {
                fighter.setMaxStreak(fighter.getStreak());
            }
        }
        fighter.setWinTimes(fighter.getWinTimes() + 1);
        fighter.setJoinTimes(fighter.getJoinTimes() + 1);
        gameUserService.updateItem(fighter);
        // 扣除长生丹、鹤龄丹、扬舞丹
        List<Integer> usedMechine = dfdjMedicineService.getMedicineToUse(winner, roomId);
        if (usedMechine.contains(TreasureEnum.ChangSD.getValue())) {
            userTreasureService.delTreasure(winner, TreasureEnum.ChangSD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.HeLD.getValue())) {
            userTreasureService.delTreasure(winner, TreasureEnum.HeLD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.YangWD.getValue())) {
            userTreasureService.delTreasure(winner, TreasureEnum.YangWD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        dfdjMedicineService.clearMedicineUseInfo(winner, roomId);
        res[0] = baseScore;
        res[1] = addedScore - baseScore;
        // 清除缓存
        TimeLimitCacheUtil.removeCache(winner, DfdjFightCache.class);
        return res;
    }

    public int handleAsDfdjFail(DfdjZone zone, long loser, long winner, int roomId) {
        // 赛季结束
        if (null == zone) {
            return 0;
        }
        DfdjFighter fighter = dfdjFighterService.getFighter(loser);
        DfdjFightCache dfdjFightCache = TimeLimitCacheUtil.getDfdjFightCache(loser);
        int loserScore = dfdjFightCache.getScore(loser);
        int loserSegment = DfdjTool.getSegmentByScore(loserScore).getId();
        int winnerScore = dfdjFightCache.getScore(winner);
        int winnerSegment = DfdjTool.getSegmentByScore(winnerScore).getId();
        // 段位扣分
        int addedScore = 0;
        CfgDfdj.ScoreAward segmentScoreAward = DfdjTool.getSegmentScoreAward(loserSegment, winnerSegment - loserSegment);
        if (segmentScoreAward != null) {
            addedScore = segmentScoreAward.getFailScore();
        }

        boolean deled = userTreasureService.delTreasure(loser, TreasureEnum.BuSD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        if (deled) {
            addedScore = 0;// 补神丹不扣分
        }
        // 积分相关处理
        if (-1 * addedScore > loserScore) {
            addedScore = -1 * loserScore;
        }
        dfdjRankService.incrementRankValue(zone, DfdjRankType.RANK, loser, addedScore);
        dfdjRankService.incrementRankValue(zone, DfdjRankType.PHASE_RANK, loser, addedScore);
        // 重置连胜
        if (fighter.getStreak() > 0) {
            fighter.setStreak(-1);
        } else {
            fighter.setStreak(fighter.getStreak() - 1);
        }
        fighter.setJoinTimes(fighter.getJoinTimes() + 1);
        gameUserService.updateItem(fighter);

        // 扣除长生丹、鹤龄丹、扬舞丹
        List<Integer> usedMechine = dfdjMedicineService.getMedicineToUse(loser, roomId);
        if (usedMechine.contains(TreasureEnum.ChangSD.getValue())) {
            userTreasureService.delTreasure(loser, TreasureEnum.ChangSD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.HeLD.getValue())) {
            userTreasureService.delTreasure(loser, TreasureEnum.HeLD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.YangWD.getValue())) {
            userTreasureService.delTreasure(loser, TreasureEnum.YangWD.getValue(), 1, WayEnum.DFDJ_FIGHT);
        }
        dfdjMedicineService.clearMedicineUseInfo(loser, roomId);
        // 清除缓存
        TimeLimitCacheUtil.removeCache(loser, DfdjFightCache.class);
        return addedScore;
    }

    /**
     * 获得随机额外的元素
     *
     * @param uid
     * @return
     */
    public RDCommon addEleAward(long uid) {
        RDCommon rd = new RDCommon();
        if (uid < 0) {
            return rd;
        }
        DfdjFighter fighter = dfdjFighterService.getFighter(uid);
        if (fighter == null) {
            return rd;
        }
        int todayInt = DateUtil.getTodayInt();
        if (fighter.getInitEleTimesDate() != todayInt) {
            fighter.setEleTimes(0);
            fighter.setInitEleTimesDate(todayInt);
        }
        if (fighter.getEleTimes() >= 10) {
            return rd;
        }
        fighter.setEleTimes(fighter.getEleTimes() + 1);
        gameUserService.updateItem(fighter);
        int num = 1;
        GameUser user = gameUserService.getGameUser(uid);
        int level = user.getLevel();
        for (int i = 26; i <= 126; i += 10) {
            if (level >= i) {
                num = (i - 6) / 10;
            } else {
                break;
            }
        }
        ResEventPublisher.pubEleAddEvent(uid, num, WayEnum.DFDJ_FIGHT, rd);
        return rd;
    }
}
