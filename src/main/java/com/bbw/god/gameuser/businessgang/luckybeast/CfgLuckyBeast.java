package com.bbw.god.gameuser.businessgang.luckybeast;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import com.bbw.god.game.config.card.CardSkillTool;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 招财兽相关配置
 *
 * @author suhq
 * @date 2019-07-29 11:17:36
 */
@Data
public class CfgLuckyBeast implements CfgInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 可重置次数 */
    private Integer resetTimesLimit;
    /** 重置消耗 */
    private Integer resetNeedBellNum;
    /** 免费攻击次数 */
    private Integer freeAttackTimes;
    /** 购买攻击次数上限 */
    private Integer buyAttackTimesLimit;
    /** 购买攻击次数 */
    private Integer buyAttackTimesNeedHonorCopperCoinNum;
    /** 参战卡牌上限 */
    private Integer cardLimit;
    /** 界碑位置 */
    private List<Integer> jieBeiPosList;
    /** 招财兽出现概率 */
    private List<LuckyBeastOutputProb> luckyBeastOutputProbs;
    /** 招财兽信息数据 */
    private List<LuckyBeastInfo> luckyBeasts;
    /** 技能分级 */
    private Map<Integer, List<String>> levelSkills;
    /** 奖励类别概率规则 */
    private Map<String, List<Integer>> awardTypeProbsRules;
    /** 奖池信息 */
    private List<awardPoolInfo> awardPoolInfos;
    /** 对招财兽有影响的法宝id */
    private List<Integer> ownEffectTReasureIds;

    /**
     * 招财兽出现概率
     *
     * @author: huanghb
     * @date: 2022/1/25 13:42
     */
    @Data
    public static class LuckyBeastOutputProb {
        /** 召唤需要的声望 */
        private Integer needPrestige;
        /** 不同招财兽出现概率 */
        private List<Integer> perLuckyBeastOutputProbs;
    }

    /**
     * 招财兽信息
     *
     * @author: huanghb
     * @date: 2022/1/25 13:42
     */
    @Data
    public static class LuckyBeastInfo {
        /** 招财兽ID */
        private Integer id;
        /** 招财兽等级 */
        private Integer level;
        /** 招财兽攻击力 */
        private Integer attackPower;
        /** 技能分级 */
        private List<Integer> skillLevels;
        /** 第一个技能池 */
        private List<Integer> firstSkillPool;
        /** 第二个技能池 */
        private List<Integer> secondSkillPool;
        /** 第三个技能池 */
        private List<Integer> thirdSkillPool;
    }

    /**
     * 奖励池信息
     *
     * @author: huanghb
     * @date: 2022/1/25 13:42
     */
    @Data
    public static class awardPoolInfo {
        private String luckyBeastAwardRule;
        private List<Integer> awardProbs;
        private List<Award> awards;
    }

    @Override
    public void prepare() {
        for (LuckyBeastInfo luckyBeastInfo : luckyBeasts) {
            //第一个技能位
            List<String> firstSkillPool = new ArrayList<>();
            if (ListUtil.isNotEmpty(luckyBeastInfo.getSkillLevels())) {
                for (Integer level : luckyBeastInfo.getSkillLevels()) {
                    firstSkillPool.addAll(levelSkills.get(level));
                }
            }
            List<Integer> firstSkillPoolIds = firstSkillPool.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            luckyBeastInfo.setFirstSkillPool(firstSkillPoolIds);
            //第二个技能位
            List<String> secondSkillPool = new ArrayList<>();
            if (ListUtil.isNotEmpty(luckyBeastInfo.getSkillLevels())) {
                for (Integer level : luckyBeastInfo.getSkillLevels()) {
                    secondSkillPool.addAll(levelSkills.get(level));
                }
            }
            List<Integer> secondSkillPoolids = secondSkillPool.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            luckyBeastInfo.setSecondSkillPool(secondSkillPoolids);
            //第三个技能位
            List<String> thirdSkillPool = new ArrayList<>();
            if (ListUtil.isNotEmpty(luckyBeastInfo.getSkillLevels())) {
                for (Integer level : luckyBeastInfo.getSkillLevels()) {
                    thirdSkillPool.addAll(levelSkills.get(level));
                }
            }
            List<Integer> thirdSkillPoolIds = thirdSkillPool.stream().map(tmp -> CardSkillTool.getSkillIdByName(tmp)).collect(Collectors.toList());
            luckyBeastInfo.setThirdSkillPool(thirdSkillPoolIds);
        }
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
