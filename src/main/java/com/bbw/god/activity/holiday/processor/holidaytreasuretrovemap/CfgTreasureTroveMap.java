package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 寻藏宝图活动配置类
 *
 * @author: huanghb
 * @date: 2022/2/8 9:50
 */
@Data
public class CfgTreasureTroveMap implements CfgInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 翻牌需要翻牌卡数量 */
    private Integer flopNeedFlopCardNum;
    /** 连线奖励数量 */
    private Integer connectionAwardNum;
    /** 寻宝道具id */
    private Integer treasureHuntPropId;
    /** 翻牌奖励 */
    private List<FlopAward> flopAwards;
    /** 连线奖励规则 */
    private List<connectionAwardRule> connectionAwardRules;
    /** 连线奖励 */
    private List<ConnectionAward> connectionAwards;
    /** 藏宝图集齐奖励 */
    private List<TreasureTroveMap> treasureTroveMaps;
    /** 目标 */
    private List<Target> targets;

    @Data
    public static class FlopAward implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        /** 翻牌奖励 */
        private List<Award> awards;
        /** 奖励概率 */
        private Integer prob;
    }

    @Data
    public static class connectionAwardRule implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 连线名称 */
        private String connectionName;
        /** 连线奖励对应下标 */
        private Integer connectionAwardIndex;
    }

    @Data
    public static class ConnectionAward implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        /** 连线奖励 */
        private List<Award> awards;
    }

    @Data
    public static class TreasureTroveMap implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        /** 藏宝图等级 */
        private Integer level;
        /** 使用该等级藏宝图的最小轮次 */
        private Integer minTurn;
        /** 使用该等级藏宝图的最大轮次 */
        private Integer maxTurn;
        /** 藏宝图碎片数量 */
        private Integer mapPieceNum;
        /** 集齐奖励 */
        private List<Award> collectAwards;
    }

    @Data
    public static class Target implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        /** 藏宝图碎片数量 */
        private Integer mapPieceNum;
        /** 目标达成奖励 */
        private List<Award> awards;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
