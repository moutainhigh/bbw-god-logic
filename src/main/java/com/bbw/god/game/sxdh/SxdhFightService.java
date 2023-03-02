package com.bbw.god.game.sxdh;

import com.bbw.god.fight.fsfight.SxdhMatchLimitService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import com.bbw.god.game.sxdh.config.SxdhRoboterType;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 神仙大会战斗服务
 *
 * @author suhq
 * @date 2019-07-04 15:22:11
 */
@Slf4j
@Service
public class SxdhFightService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhDateService sxdhDateService;
    @Autowired
    private SxdhFighterService sxdhFighterService;
    @Autowired
    private SxdhMechineService sxdhMechineService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private SxdhRoboterService sxdhRoboterService;
    @Autowired
    private SxdhMatchLimitService sxdhMatchLimitService;

    public int[] handleAsSxdhWin(SxdhZone sxdhZone, long winner, long loser, int roomId, SxdhRoboterType roboterType, int zcNum) {
        //总得分，额外分数
        int[] res = {0, 0};
        //机器人或者赛季结束
        if (winner <= 0 || sxdhZone == null) {
            return res;//总得分，额外分数
        }

        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(winner);
        // 设置连胜
        if (sxdhFighter.getStreak() < 0) {
            sxdhFighter.setStreak(1);
        } else {
            sxdhFighter.setStreak(sxdhFighter.getStreak() + 1);
            if (sxdhFighter.getMaxStreak() < sxdhFighter.getStreak()) {
                sxdhFighter.setMaxStreak(sxdhFighter.getStreak());
            }
        }
        sxdhFighter.setWinTimes(sxdhFighter.getWinTimes() + 1);
        sxdhFighter.setJoinTimes(sxdhFighter.getJoinTimes() + 1);
        gameUserService.updateItem(sxdhFighter);

        if (SxdhTool.isSpecialSeason()) {
            sxdhMatchLimitService.addMatchedTimes(winner, sxdhZone);
            res = getAddScoreForWinAsSpecialSeason(sxdhFighter, zcNum);
        } else {
            res = getAddScoreForWin(sxdhZone, winner, loser, roboterType, zcNum);
        }
        int addedScore = res[0] + res[1];
        // 处理排行
        sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.RANK, winner, addedScore);
        sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.PHASE_RANK, winner, addedScore);
        // 扣除长生丹、鹤龄丹、扬舞丹
        handleMechine(winner, roomId);
        return res;
    }

    public int handleAsSxdhFail(SxdhZone sxdhZone, long loser, long winner, int roomId, SxdhRoboterType roboterType) {
        //机器人或者赛季结束
        if (loser <= 0 || sxdhZone == null) {
            return 0;
        }
        int addedScore = 0;
        if (SxdhTool.isSpecialSeason()) {
            sxdhMatchLimitService.addMatchedTimes(loser, sxdhZone);
            addedScore = getAddScoreForLoserAsSpecialSeason(loser);
        } else {
            addedScore = getAddScoreForLoser(sxdhZone, loser, winner, roboterType);
        }
        // 积分相关处理
        sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.RANK, loser, addedScore);
        sxdhRankService.incrementRankValue(sxdhZone, SxdhRankType.PHASE_RANK, loser, addedScore);

        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(loser);
        // 重置连胜
        if (sxdhFighter.getStreak() > 0) {
            sxdhFighter.setStreak(-1);
        } else {
            sxdhFighter.setStreak(sxdhFighter.getStreak() - 1);
        }
        sxdhFighter.setJoinTimes(sxdhFighter.getJoinTimes() + 1);
        gameUserService.updateItem(sxdhFighter);

        // 扣除长生丹、鹤龄丹、扬舞丹
        handleMechine(loser, roomId);
        return addedScore;
    }


    /**
     * 获取胜利加分
     *
     * @param sxdhZone
     * @param winner
     * @param loser
     * @param roboterType
     * @param zcNum
     * @return [总得分, 额外分数]
     */
    private int[] getAddScoreForWin(SxdhZone sxdhZone, long winner, long loser, SxdhRoboterType roboterType, int zcNum) {
        int[] res = {0, 0};//总得分，额外分数
        int winnerScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, winner);
        int winnerSegment = SxdhTool.getSegmentByScore(winnerScore).getId();
        int winnerRank = sxdhRankService.getRank(sxdhZone, SxdhRankType.PHASE_RANK, winner);
        int loserScore = 0;
        int loserSegment = winnerSegment;
        int loserRank = winnerRank;
        boolean hasRankScore = true;
        switch (roboterType) {
            case REAL:
                loserScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, loser);
                loserSegment = SxdhTool.getSegmentByScore(loserScore).getId();
                loserRank = sxdhRankService.getRank(sxdhZone, SxdhRankType.PHASE_RANK, winner);
                break;
            case ONE:
                Long roboterId = sxdhRoboterService.getOneRoboter(winner);
                if (roboterId != null) {
                    loserScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, roboterId);
                    loserSegment = SxdhTool.getSegmentByScore(loserScore).getId();
                    loserRank = sxdhRankService.getRank(sxdhZone, SxdhRankType.PHASE_RANK, roboterId);
                }
            default:
                hasRankScore = false;
                break;
        }
        //段位加分
        int segmentGap = loserSegment - winnerSegment;
        int segmentScore = 0;
        CfgSxdh.ScoreAward segmentScoreAward = SxdhTool.getSegmentScoreAward(segmentGap);
        if (segmentScoreAward != null) {
            segmentScore = segmentScoreAward.getWinScore();
        }
        //机器人段位加分减半
        if (!SxdhRoboterType.REAL.equals(roboterType)) {
            segmentScore = segmentScore / 2;
        }
        //排名加分
        int rankScore = 0;
        if (hasRankScore) {
            CfgSxdh.ScoreAward rankScoreAward = SxdhTool.getRankScoreAward(winnerRank - loserRank);
            if (rankScoreAward != null) {
                rankScore = rankScoreAward.getWinScore();
            }
        }

        int baseScore = rankScore + segmentScore;
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        if (seasonPhase != null) {
            baseScore *= seasonPhase.getDoubles();
        }
        int addedScore = baseScore;
        if (zcNum > 0) {
            addedScore += zcNum;
        }
        boolean deled = userTreasureService.delTreasure(winner, TreasureEnum.YuanQD.getValue(), 1, WayEnum.SXDH_FIGHT);
        if (deled) {
            addedScore *= 2;// 元气丹分翻倍
        }
        res[0] = baseScore;
        res[1] = addedScore - baseScore;
        return res;
    }

    /**
     * 获取失败积分
     *
     * @param sxdhZone
     * @param loser
     * @param winner
     * @param roboterType
     * @return
     */
    private int getAddScoreForLoser(SxdhZone sxdhZone, long loser, long winner, SxdhRoboterType roboterType) {
        int loserScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, loser);
        int loserSegment = SxdhTool.getSegmentByScore(loserScore).getId();
        int winnerScore = 0;
        int winnerSegment = loserSegment;
        switch (roboterType) {
            case REAL:
                winnerScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, winner);
                winnerSegment = SxdhTool.getSegmentByScore(winnerScore).getId();
                break;
            case ONE:
                Long roboterId = sxdhRoboterService.getOneRoboter(loser);
                if (roboterId != null) {
                    winnerScore = sxdhRankService.getScore(sxdhZone, SxdhRankType.PHASE_RANK, roboterId);
                    winnerSegment = SxdhTool.getSegmentByScore(winnerScore).getId();
                }
            default:
                break;
        }
        int segmentGap = winnerSegment - loserSegment;
        int addedScore = 0;
        CfgSxdh.ScoreAward segmentScoreAward = SxdhTool.getSegmentScoreAward(segmentGap);
        if (segmentScoreAward != null) {
            addedScore = segmentScoreAward.getFailScore();
        }

        boolean deled = userTreasureService.delTreasure(loser, TreasureEnum.BuSD.getValue(), 1, WayEnum.SXDH_FIGHT);
        if (deled) {
            addedScore = 0;// 补神丹不扣分
        }
        return addedScore;
    }

    /**
     * 扣除长生丹、鹤龄丹、扬舞丹
     *
     * @param uid
     * @param roomId
     */
    private void handleMechine(long uid, int roomId) {
        // 扣除长生丹、鹤龄丹、扬舞丹
        List<Integer> usedMechine = sxdhMechineService.getMechinesToUse(uid, roomId);
        if (usedMechine.contains(TreasureEnum.ChangSD.getValue())) {
            userTreasureService.delTreasure(uid, TreasureEnum.ChangSD.getValue(), 1, WayEnum.SXDH_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.HeLD.getValue())) {
            userTreasureService.delTreasure(uid, TreasureEnum.HeLD.getValue(), 1, WayEnum.SXDH_FIGHT);
        }
        if (usedMechine.contains(TreasureEnum.YangWD.getValue())) {
            userTreasureService.delTreasure(uid, TreasureEnum.YangWD.getValue(), 1, WayEnum.SXDH_FIGHT);
        }
        sxdhMechineService.clearMechineUseInfo(uid, roomId);
    }


    /**
     * 特殊赛季奖励积分
     *
     * @param sxdhFighter
     * @param zcNum
     * @return
     */
    private int[] getAddScoreForWinAsSpecialSeason(SxdhFighter sxdhFighter, int zcNum) {
        int baseScore = 2 * sxdhFighter.getStreak();
        baseScore = baseScore > 20 ? 20 : baseScore;
        int addedScore = baseScore;
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        if (seasonPhase != null) {
            addedScore *= seasonPhase.getDoubles();
        }
        if (zcNum > 0) {
            addedScore += zcNum;
        }
        boolean deled = userTreasureService.delTreasure(sxdhFighter.getGameUserId(), TreasureEnum.YuanQD.getValue(), 1, WayEnum.SXDH_FIGHT);
        if (deled) {
            addedScore *= 2;// 元气丹分翻倍
        }
        int[] res = {0, 0};//总得分，额外分数
        res[0] = baseScore;
        res[1] = addedScore - baseScore;
        return res;
    }

    /**
     * 特殊赛季获取失败积分
     *
     * @param loser
     * @return
     */
    private int getAddScoreForLoserAsSpecialSeason(long loser) {
        int addedScore = -8;
        boolean deled = userTreasureService.delTreasure(loser, TreasureEnum.BuSD.getValue(), 1, WayEnum.SXDH_FIGHT);
        if (deled) {
            addedScore = 0;// 补神丹不扣分
        }
        return addedScore;
    }

}
