package com.bbw.god.game.dfdj.config;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.dfdj.DfdjDateService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DfdjTool {
    private static final DfdjDateService DFDJ_DATE_SERVICE = SpringContextUtil.getBean(DfdjDateService.class);

    public static CfgDfdjMedicineEntity getMedicine(int medicineId) {
        Optional<CfgDfdjMedicineEntity> optional = getDfdj().getMedicines().stream().filter(tmp -> tmp.getId() == medicineId).findFirst();
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
    public static List<CfgDfdjZoneEntity> getZones(int maxOpenDay) {
        List<CfgDfdjZoneEntity> allZones = getDfdj().getZones();
        return allZones.stream().filter(tmp -> tmp.getOpenAfterDay() <= maxOpenDay).collect(Collectors.toList());
    }

    public static CfgDfdj getDfdj() {
        return Cfg.I.getUniqueConfig(CfgDfdj.class);
    }

    /**
     * 根据积分获得段位
     *
     * @param score
     * @return
     */
    public static CfgDfdjSegmentEntity getSegmentByScore(int score) {
        return getSegments().stream().filter(tmp -> score >= tmp.getMinScore() && score <= tmp.getMaxScore()).findFirst().get();
    }

    public static CfgDfdjSegmentEntity getSegment(int segment) {
        return getSegments().stream().filter(tmp -> tmp.getId() == segment).findFirst().get();
    }

    public static CfgDfdjStageEntity getStageBySegment(int segment) {
        CfgDfdjSegment cfgDfdjSegment = Cfg.I.getUniqueConfig(CfgDfdjSegment.class);
        return cfgDfdjSegment.getStages().stream().filter(tmp -> tmp.getSegments().contains(segment)).findFirst().get();
    }

    public static CfgDfdj.ScoreAward getSegmentScoreAward(int segment, int segmentGap) {
        final int stage = getStageBySegment(segment).getId();
        return getDfdj().getSegmentScoreAwards().stream().filter(tmp -> tmp.isInStage(stage) && tmp.isInGap(segmentGap)).findFirst().orElse(null);
    }

    public static CfgDfdj.ScoreAward getRankScoreAward(int gap) {
        return getDfdj().getRankScoreAwards().stream().filter(tmp -> gap >= tmp.getMinGap() && gap <= tmp.getMaxGap()).findFirst().orElse(null);
    }

    public static CfgDfdjRankAwardEntity getDfdjRankAwardEntity(int rank, DfdjRankType rankType) {
        List<CfgDfdjRankAwardEntity> awardEntityList = getRankAwards(rankType);
        CfgDfdjRankAwardEntity awardEntity = awardEntityList.stream().filter(dayAward -> dayAward.getMaxRank() >= rank
                && dayAward.getMinRank() <= rank).findFirst().orElse(null);
        return awardEntity;
    }

    public static List<CfgDfdjRankAwardEntity> getRankAwards(DfdjRankType rankType) {
        CfgDfdjRankAward dfdjRankAward = Cfg.I.getUniqueConfig(CfgDfdjRankAward.class);
        switch (rankType) {
            case PHASE_RANK:
            case LAST_PHASE_RANK:
                return dfdjRankAward.getPhaseAwards();
            case MIDDLE_RANK:
                return dfdjRankAward.getMiddleSeasonAwards();
            default:
                return dfdjRankAward.getSeasonAwards();
        }
    }

    private static List<CfgDfdjSegmentEntity> getSegments() {
        CfgDfdjSegment cfgDfdjSegment = Cfg.I.getUniqueConfig(CfgDfdjSegment.class);
        CfgDfdj.SeasonPhase seasonPhase = DFDJ_DATE_SERVICE.getCurSeasonPhase();
        if ("冲刺".equals(seasonPhase.getSegments())) {
            return cfgDfdjSegment.getSprintSegments();
        }
        return cfgDfdjSegment.getSegments();
    }
}
