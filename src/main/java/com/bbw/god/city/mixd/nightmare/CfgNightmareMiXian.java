package com.bbw.god.city.mixd.nightmare;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：梦魇迷仙洞
 *
 * @author lwb
 * date 2021-05-26
 */
@Data
public class CfgNightmareMiXian implements CfgInterface {
    private String key;
    /** 层主最小卡牌数 */
    private Integer levelOwnerMinCardNum;
    /**
     * 最大挑战层数
     */
    private Integer maxChallengeLayers;
    /**
     * 每20分钟可恢复1层（单位：秒）
     */
    private Integer incLayersSpeedSecond;
    /**
     * 宝箱
     */
    private List<BoxInfo> boxAwards;

    /**
     * 每层的生成数据规则
     */
    private List<LevelDataRule> levelDataRules;

    /**
     * 概率组（铜钱、 卡牌、元素、元宝、法宝），根据每日元宝累计数
     */
    private List<ProbabilityGroup> probabilityGroups;

    /**
     * 熔炉奖励
     */
    private List<AwardInfo> smeltAwards;
    /**
     * 姜环卡组
     */
    private List<CardParam> jiangHuanCards;
    /**
     * 烛龙卡组
     */
    private List<CardParam> zhuLongCards;

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }

    /**
     * 获取随机奖励
     * @return
     */
    public List<Award> getRandomBoxAwards(boolean extra){
        int max = boxAwards.stream().collect(Collectors.summingInt(x -> x.getProbability())).intValue();
        int seed = PowerRandom.getRandomBySeed(max);
        int sum=0;
        BoxInfo boxInfo=boxAwards.get(0);
        for (BoxInfo info : boxAwards) {
            sum+=info.getProbability();
            if (sum>=seed){
                boxInfo=info;
                break;
            }
        }
        List<Award> awards=boxInfo.getRandomSubAwards();
        if (extra){
            awards.addAll(boxInfo.getRandomSubAwards());
        }
        return awards;
    }

    /**
     * 宝箱信息
     */
    @Data
    public static class BoxInfo implements Serializable{
        private static final long serialVersionUID = 3833292293953584833L;
        private Integer probability;
        private Integer awardEnum;
        private List<AwardInfo> awardInfo;

        /**
         * 获取奖励
         * @return
         */
        public List<Award> getRandomSubAwards(){
            int max = awardInfo.stream().collect(Collectors.summingInt(x ->x.getProbability())).intValue();
            int seed = PowerRandom.getRandomBySeed(max);
            int sum=0;
            for (AwardInfo info : awardInfo) {
                sum+=info.getProbability();
                if (sum>=seed){
                    return info.getAwards();
                }
            }
            return awardInfo.get(0).getAwards();
        }
    }

    /**
     * 宝箱具体奖励分布
     */
    @Data
    public static class AwardInfo implements Serializable{
        private static final long serialVersionUID = 1361568574058965267L;
        private Integer probability;
        private List<Award> awards;
    }

    /**
     * 概率组（铜钱、 卡牌、元素、元宝、法宝），根据每日元宝累计数
     */
    @Data
    public static class ProbabilityGroup implements Serializable{
        private final long serialVersionUID = 1361568574058965267L;
        // 元宝累计值（最小）
        private Integer minDailyGoldNum;
        // 元宝累计值（最大）
        private Integer maxDailyGoldNum;
        // 奖励概率集合
        private List<ProbabilityAward> probabilities;

        @Data
        public static class ProbabilityAward{
            // 奖励类型
            private Integer item;
            // 概率
            private Integer probability;
        }
    }

    /**
     * 关卡数据规则
     */
    @Data
    public static class LevelDataRule implements Serializable{
        private static final long serialVersionUID = -5524258127397788249L;
        private Integer minLevel;
        private Integer maxLevel;
        private List<LevelData> posData;
    }

    /**
     * 关卡数据
     */
    @Data
    public static class LevelData implements Serializable{
        private static final long serialVersionUID = 8892123728196505160L;
        /**
         * 类型 对应NightmareMiXianPosEnum
         */
        private Integer type;
        /**
         * 最少
         */
        private Integer least;
        /**
         * 最多
         */
        private Integer most;


        public NightmareMiXianPosEnum getMiXianPosEnum(){
            return NightmareMiXianPosEnum.fromType(type);
        }
    }
    @Data
    public static class CardParam implements Serializable{
        private static final long serialVersionUID = 5032735402071378677L;
        private Integer id;
        private Integer skill0;
        private Integer skill5;
        private Integer skill10;
        private Integer useScroll;
    }
}