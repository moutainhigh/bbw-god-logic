package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.server.maou.bossmaou.BossMaouLevel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 魔王boss相关配置
 */
@Data
public class CfgBossMaou implements CfgInterface, Serializable {
    private static final long serialVersionUID = -926676412865385950L;
    private String key;
    private Integer cardLimitPerType;
    private Integer goldToAttack;
    private Integer bloodRateToActiveDoubleAttack;
    private Integer goldToDoubleAttack;
    private Integer doubleTimes;
    private Integer rankerNumToShow;
    private List<BossMaou> maous;
    private Integer minBlood;
    private Integer maxBlood;
    private List<BloodRule> bloodRules;
    private List<String> killerAwards;
    private List<Integer> specialCardDates;
    private List<RankerAward> rankerAwards;

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
    public static class BossMaou implements Serializable {
        private static final long serialVersionUID = -6640491105918072743L;
        private Integer id;
        private Integer maouLevel;
        private Integer beginTime;
        private Integer attackTime;
        private Integer endTime;
        private Integer roudToChangeType;
        private Integer totalRound;
        private Integer timePerRound;
        private List<Integer> aloneMaouLevelsEnable;
        private Integer initBlood;
        private String memo;
    }

    @Data
    public static class BloodRule implements Serializable {
        private static final long serialVersionUID = -3663126312607539958L;
        private Integer minTime;
        private Integer maxTime;
        private Integer minlostBloodRate;
        private Integer maxlostBloodRate;
        private Integer bloodInc;
    }

    @Data
    public static class RankerAward implements Serializable {
        private static final long serialVersionUID = 8359795233497192097L;
        private Integer maouLevel;
        private Integer minRank;
        private Integer maxRank;
        private List<Award> awards;

        public boolean ifMatch(BossMaouLevel maouLevel, int rank) {
            return this.maouLevel == maouLevel.getValue() && rank >= this.minRank && rank <= this.maxRank;
        }
    }
}
