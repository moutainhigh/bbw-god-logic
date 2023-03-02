package com.bbw.god.game.sxdh.config;

import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.sxdh.SxdhDateService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SxdhTool {
    private static SxdhDateService sxdhDateService = SpringContextUtil.getBean(SxdhDateService.class);

    public static CfgSxdhMedicineEntity getMedicine(int medicineId) {
        Optional<CfgSxdhMedicineEntity> optional = getSxdh().getMedicines().stream().filter(tmp -> tmp.getId() == medicineId).findFirst();
        if (!optional.isPresent()) {
            throw CoderException.high("无效的丹药" + medicineId);
        }
        return optional.get();
    }

    /**
     * 获得可用战区
     *
     * @param maxOpenDay
     * @return
     */
    public static List<CfgSxdhZoneEntity> getZones(int maxOpenDay) {
        List<CfgSxdhZoneEntity> allZones = getSxdh().getZones();
        List<CfgSxdhZoneEntity> zones = allZones.stream().filter(tmp -> tmp.getOpenAfterDay() <= maxOpenDay).collect(Collectors.toList());
        return zones;
    }

    public static CfgSxdh getSxdh() {
        return Cfg.I.getUniqueConfig(CfgSxdh.class);
    }

    /**
     * 是否是特殊赛季
     * //TODO 临时，后续如果有再修改比较靠谱的特殊机制需要重构下
     *
     * @return
     */
    public static boolean isSpecialSeason() {
        return 202210 == DateUtil.toMonthInt(DateUtil.now());
    }

    /**
     * 根据积分获得段位
     *
     * @param score
     * @return
     */
    public static CfgSxdhSegmentEntity getSegmentByScore(int score) {
        return getSegments().stream().filter(tmp -> score >= tmp.getMinScore() && score <= tmp.getMaxScore()).findFirst().get();
    }

    public static CfgSxdhSegmentEntity getSegment(int segment) {
        return getSegments().stream().filter(tmp -> tmp.getId() == segment).findFirst().get();
    }

    public static CfgSxdhStageEntity getStageBySegment(int segment) {
        CfgSxdhSegment cfgSxdhSegment = Cfg.I.getUniqueConfig(CfgSxdhSegment.class);
        return cfgSxdhSegment.getStages().stream().filter(tmp -> tmp.getSegments().contains(segment)).findFirst().get();
    }

    public static CfgSxdh.ScoreAward getSegmentScoreAward(int gap) {
        return getSxdh().getSegmentScoreAwards().stream().filter(tmp -> gap >= tmp.getMinGap() && gap <= tmp.getMaxGap()).findFirst().orElse(null);
    }

    public static CfgSxdh.ScoreAward getRankScoreAward(int gap) {
        return getSxdh().getRankScoreAwards().stream().filter(tmp -> gap >= tmp.getMinGap() && gap <= tmp.getMaxGap()).findFirst().orElse(null);
    }

    public static CfgSxdhRankAwardEntity getSxdhRankAwardEntity(int rank, SxdhRankType rankType) {
        List<CfgSxdhRankAwardEntity> awardEntityList = getRankAwards(rankType);
        CfgSxdhRankAwardEntity awardEntity = awardEntityList.stream().filter(dayAward -> dayAward.getMaxRank() >= rank
                && dayAward.getMinRank() <= rank).findFirst().orElse(null);
        return awardEntity;
    }

    public static List<CfgSxdhRankAwardEntity> getRankAwards(SxdhRankType rankType) {
        CfgSxdhRankAward sxdhRankAward = Cfg.I.getUniqueConfig(CfgSxdhRankAward.class);
        switch (rankType) {
            case PHASE_RANK:
            case LAST_PHASE_RANK:
                return sxdhRankAward.getPhaseAwards();
            case MIDDLE_RANK:
                return sxdhRankAward.getMiddleSeasonAwards();
            default:
                return sxdhRankAward.getSeasonAwards();
        }
    }

    private static List<CfgSxdhSegmentEntity> getSegments() {
        CfgSxdhSegment cfgSxdhSegment = Cfg.I.getUniqueConfig(CfgSxdhSegment.class);
        CfgSxdh.SeasonPhase seasonPhase = sxdhDateService.getCurSeasonPhase();
        if ("冲刺".equals(seasonPhase.getSegments())) {
            return cfgSxdhSegment.getSprintSegments();
        }
        return cfgSxdhSegment.getSegments();
    }
}
