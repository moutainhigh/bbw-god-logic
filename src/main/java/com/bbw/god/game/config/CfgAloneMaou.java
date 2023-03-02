package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 独战魔王相关配置
 */
@Data
public class CfgAloneMaou implements CfgInterface, Serializable {
    private static final long serialVersionUID = 2631369485386290071L;
    public static final Integer UNLIMIT_ROUND = 99999;
    private String key;
    private Integer beginTime;
    private Integer endTime;
    private Integer resetTimesLimit;
    private Integer resetGold;
    private Integer freeAttackTimes;
    private Integer cardLimit;
    private List<AloneMaou> maous;
    private List<MaouDiceProb> maouDiceProbs;
    private List<MaouLevelAward> maouLevelAwards;
    // 生效的技能
    private List<Integer> effectSkills;
    // 生效的组合
    private List<Integer> effectGroups;

    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class AloneMaou implements Serializable {
        private static final long serialVersionUID = -746674403511277956L;
        private Integer id;
        private Integer level;
        private Integer blood;
        private Integer shieldDefence;
        private Integer shield;
        private List<Integer> mauSkills;
        private Integer limitRound;
    }

    @Data
    public static class MaouDiceProb implements Serializable {
        private static final long serialVersionUID = -6923916230385754347L;
        private Integer minAccBlood;
        private Integer maxAccBlood;
        private Integer prob;
    }

    @Data
    public static class MaouLevelAward implements Serializable {
        private static final long serialVersionUID = -4308048923907687730L;
        private Integer maouLevel;
        private Integer type;
        private List<Award> awards;
    }

}
