package com.bbw.god.gameuser.card.equipment.cfg;

import com.bbw.god.gameuser.card.equipment.CardEquipmentService;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 卡牌战斗加成
 *
 * @author: huanghb
 * @date: 2022/9/22 11:15
 */
@NoArgsConstructor
@Data
public class CardFightAddition implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer cardId;
    /** 攻击 效果为属性加值 即1点攻击对应一点攻击值 */
    private Integer attack = 0;
    /** 防御 效果为属性加值 即1点防御对应一点防御值 */
    private Integer defence = 0;
    /** 技能组  值为技能id */
    private List<SkillAddition> skillAdditions = new ArrayList<>();

    public CardFightAddition(int cardId, Map<Integer, Integer> typeAdditions) {
        Integer attckAddition = typeAdditions.getOrDefault(CardEquipmentAdditionEnum.ATTACK.getValue(), 0);
        Integer defenceAddition = typeAdditions.getOrDefault(CardEquipmentAdditionEnum.DEFENSE.getValue(), 0);
        this.cardId = cardId;
        this.attack = attckAddition;
        this.defence = defenceAddition;
    }

    /**
     * 获得加成效果 下标0 触发概率 下标1 触发加成 住 触发概率和触发加成都要*技能上限才是最终值
     *
     * @param skillId
     * @return
     */
    public SkillAddition getSkillAddition(Integer skillId) {
        SkillAddition skillAddition = skillAdditions.stream().filter(tmp -> tmp.getSkillId().equals(skillId)).findFirst().orElse(null);
        return skillAddition;
    }

    /**
     * 触发概率：（灵宝技能效果[概率]上限%/500）*（至宝上的韧度*（1+参悟韧度加成%）
     * 效果加成：（灵宝技能效果[概率]上限%/500）*（至宝上的强度*（1+参悟强度加成%）
     *
     * @param skillId
     * @param strengthRate
     * @param tenacityRate
     * @param strength
     * @param tenacity
     */
    public void addSkillAddition(Integer skillId, int strengthRate, int tenacityRate, int strength, int tenacity) {
        SkillAddition maxAddition = CardEquipmentService.SKILL_MAX_RATE.get(skillId);
        if (null == maxAddition) {
            return;
        }
        double performProbablity = (maxAddition.getPerformProbability() / 500) * tenacity * (1 + tenacityRate / 100.0);
        double extraRate = (maxAddition.getExtraRate() / 500) * strength * (1 + strengthRate / 100.0);
        SkillAddition skillAddition = new SkillAddition(skillId, performProbablity, extraRate);
        skillAdditions.add(skillAddition);
    }

    /**
     * 技能效果加成
     *
     * @author: suhq
     * @date: 2022/9/29 2:05 下午
     */
    @Data
    public static class SkillAddition {
        /** 技能ID */
        private Integer skillId;
        /** 技能触发概率 */
        private Double performProbability;
        /** 技能效果加成 */
        private Double extraRate;

        public SkillAddition(Integer skillID, Double performProbability, Double extraRate) {
            this.skillId = skillID;
            this.performProbability = performProbability;
            this.extraRate = extraRate;
        }

        public SkillAddition(Double performProbability, Double extraRate) {
            this.performProbability = performProbability;
            this.extraRate = extraRate;
        }

    }
}
