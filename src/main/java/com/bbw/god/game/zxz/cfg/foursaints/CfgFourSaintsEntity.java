package com.bbw.god.game.zxz.cfg.foursaints;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 四圣挑战配置
 *
 * @author: hzf
 * @create: 2022-12-26 10:26
 **/
@Data
public class CfgFourSaintsEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    private String key;
    /** 金创药使用次数 */
    private Integer jinCYLimitNum;
    /**复活草使用次数 */
    private Integer fuHCLimitNum;
    /** 免费刷新次数 */
    private Integer freeRefreshFrequency;
    /** 四圣整体规则 */
    private List<CfgFourSaintsChallenge> fourSaintsChallenges;
    /** 四圣野怪生成规则 */
    private List<CfgFourSaintsDefenderCardRule> defenderCardRules;
    /**诛仙阵要过滤对的符图id */
    private List<Integer> filterFutuIds;
    /** 诛仙阵要过滤的词条 */
    private List<Integer> filterEntryIds;
    /** 词条随机规则 */
    private List<CfgEntryRandom> entryRandoms;
    /** 奖励规则 */
    private List<CfgFourSaintsAwad> fourSaintsAwads;
    /** 自动刷新规则 */
    private CfgAutoRefreshRule autoRefreshRules;
    /** 词条随机加等级规则 */
    private List<CfgEntryRandomLv> entryRandomAddLvRules;
    /** 四圣挑战敌方召唤师的头像和名称*/
    private List<CfgNameAndHeadImg> nameAndHeadImgRules;
    /** 随机卡牌位置技能不变 */
    private List<CfgRandomCardSkillLimit>randomCardSkillLimitRules;

    @Data
    public static class CfgFourSaintsChallenge {
        /** 挑战类型 */
        private Integer challengeType;
        /** 解锁条件 分数 */
        private Integer unlockNeedScore;
        /** 解锁条件 难度*/
        private Integer unlockDifficulty;
        /** 属性限制编组卡牌 */
        private List<Integer> attributeLimit;
        /** 随机属性 */
        private List<Integer> randomAttributeLimit;
        /** 减少卡牌等级 */
        private Integer reduceCardLv;
        /** 灵装词条等级 */
        private Integer lingCEntryLv;
    }

    /**
     * 四圣挑战 野怪的随机规则
     */
    @Data
    public static class CfgFourSaintsDefenderCardRule {
        /** 关卡id */
        private Integer defenderId;
        /** 挑战类型 */
        private Integer challengeType;
        /** 野怪种类 */
        private Integer kind;
        /** 召唤师等级 */
        private Integer summonerLv;
        /** 野怪血条 */
        private Integer bloodBarNum;
        /** 卡牌等级 */
        private Integer cardLv;
        /** 卡牌阶级 */
        private Integer cardHv;
        /** 卡牌技能随机 */
        private Integer skillRandom;
        /** 符图随机 */
        private List<CfgFourSaintsFuTus> fuTus;
        /** 卡牌数据 */
        private List<FightCardGenerateRule> cards;
    }

    /**
     * 四圣挑战 符图随机规则
     */
    @Data
    public static class CfgFourSaintsFuTus {
        /** 符图类型 */
        private List<Integer> fuTuTypes;
        /** 符图的品质 */
        private List<Integer> fuTuQualitys;
    }

    @Data
    public static class CfgFourSaintsAwad {
        /** 挑战类型 */
        private Integer challengeType;
        /** 探索点 */
        private Integer exploratoryPoint;
        /** 奖励 */
        private Award award;
    }
    @Data
    public static class CfgEntryRandom{
        /** 挑战类型 */
        private Integer challengeType;
        /** 词条等级上限 */
        private Integer lvUpperLimit;
        /** 存量等级 */
        private Integer lvStock;
    }
    @Data
    public static class CfgAutoRefreshRule{
        /** 周几 */
        private Integer weekDay;
        /** 几点 */
        private Integer hour;
    }
    @Data
    public static class CfgEntryRandomLv{
        /** 加值 */
        private Integer addLv;
        /** 概率 */
        private Integer probability;
    }
    @Data
    public static class CfgNameAndHeadImg{
        /** 挑战类型 */
        private Integer challengeType;
        /** 野怪种类 */
        private Integer kind;
        /** 名称 */
        private String name;
        /** 头像 */
        private Integer headImg;
    }
    @Data
    public static class CfgRandomCardSkillLimit{
        /** 卡牌id */
        private Integer cardId;
        /** 技能位置 */
        private Integer pos;
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
