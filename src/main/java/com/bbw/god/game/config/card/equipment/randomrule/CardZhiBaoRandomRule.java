package com.bbw.god.game.config.card.equipment.randomrule;

import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.XianJueTypeEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.kunls.CfgKunLSTool;
import com.bbw.god.gameuser.kunls.cfg.CfgKunLS;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡牌至宝基本随机规则
 * @author: hzf
 * @create: 2022-12-13 10:42
 **/
@Data
public class CardZhiBaoRandomRule implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 卡牌id */
    private Integer cardId;
    /** 至宝ID */
    private Integer zhiBaoId;
    /** 五行属性 */
    private Integer property = TypeEnum.Null.getValue();
    /** 加成  加成类型=》加成值 */
    private Map<String, Integer> additions = new HashMap<>();
    /** 技能组 */
    private Integer[] skillGroup;

    public static CardZhiBaoRandomRule getInstance(Integer cardId, CfgCardEntity cfgCard,CfgCardEquipmentRandomRuleEntity.CfgZhiBao zhiBao){
        CardZhiBaoRandomRule cardZhiBao = new CardZhiBaoRandomRule();
        cardZhiBao.setCardId(cardId);
        cardZhiBao.setZhiBaoId(zhiBao.getZhiBaoId());
        cardZhiBao.setProperty(cfgCard.getType());
        cardZhiBao.setAdditions(zhiBao.getAdditions());
        cardZhiBao.setSkillGroup(randomSkillGroup(cardZhiBao.getZhiBaoId()));
        return cardZhiBao;
    }

    /**
     * 存储方式
     * @return
     */
    @Override
    public String toString() {
        String SPLIT_CHAR = "@";
        String additionMaps = CfgCardEquipmentRandomRuleTool.convertWithStream(additions);
        String skillStr = "";
        //构建技能组
        for (Integer skill : skillGroup) {
            if (skillStr.length() != 0) {
                skillStr += ",";
            }
            skillStr += skill;
        }
        return cardId + SPLIT_CHAR + zhiBaoId + SPLIT_CHAR + property + SPLIT_CHAR
                + additionMaps + SPLIT_CHAR + skillStr;
    }

    /**
     * 构建卡牌至宝
     * @param cardIds
     * @return
     */
    public static List<CardZhiBaoRandomRule> instanceCardZhiBao(List<Integer> cardIds){
        if (ListUtil.isEmpty(cardIds)) {
            return new ArrayList<>();
        }
        List<CardZhiBaoRandomRule> cardZhiBaos = new ArrayList<>();
        for (Integer cardId : cardIds) {
            //获取卡牌的基础属性
            CfgCardEntity cfgCard = CardTool.getCardById(cardId);
            if (null == cfgCard) {
                continue;
            }
            List<CfgCardEquipmentRandomRuleEntity.CfgZhiBao> cfgZhiBaos = CfgCardEquipmentRandomRuleTool.getCfgZhiBaos(FightTypeEnum.ZXZ.getValue());
            for (CfgCardEquipmentRandomRuleEntity.CfgZhiBao zhiBao : cfgZhiBaos) {
                CardZhiBaoRandomRule cardZhiBao = CardZhiBaoRandomRule.getInstance(cardId, cfgCard, zhiBao);
                cardZhiBaos.add(cardZhiBao);
            }
        }
        return cardZhiBaos;
    }

    /***
     * 将卡牌转化成字符串
     * @param cardIds
     * @return
     */
    public static List<String> instances(List<Integer> cardIds) {
        List<CardZhiBaoRandomRule> cardZhiBaos = instanceCardZhiBao(cardIds);
        List<String> cardZhiBaoStrings = new ArrayList<>();
        for (CardZhiBaoRandomRule cardZhiBao : cardZhiBaos) {
            cardZhiBaoStrings.add(cardZhiBao.toString());
        }
        return cardZhiBaoStrings;
    }

    /**
     * 获取到随机技能
     * @param zhiBaoId
     * @return
     */
    public static Integer[] randomSkillGroup(Integer zhiBaoId){
        List<CfgKunLS.Property> infusions = CfgKunLSTool.getKunLSConfig().getInfusions();
        CfgKunLS.Property property = infusions.stream().filter(tmp -> tmp.getId().equals(zhiBaoId)).findFirst().orElse(null);
        if (null == property) {
            return new Integer[]{0, 0};
        }
        Integer randomSkillGroupOne = PowerRandom.getRandomFromList(property.getSkillGroupOne());
        Integer randomSkillGroupTwo = PowerRandom.getRandomFromList(property.getSkillGroupTwo());
        Integer[] skillGroup = new Integer[2];
        skillGroup[0]=randomSkillGroupOne;
        skillGroup[1]=randomSkillGroupTwo;
        return skillGroup;
    }
    /**
     * 获取至宝加成
     *
     * @return
     */
    public List<CardEquipmentAddition> gainAdditions() {
        List<CardEquipmentAddition> cardAdditions = new ArrayList<>();
        if (MapUtil.isEmpty(additions)) {
            return cardAdditions;
        }
        for (Map.Entry<String, Integer> entry : additions.entrySet()) {
            cardAdditions.add(new CardEquipmentAddition(Integer.valueOf(entry.getKey()), entry.getValue()));
        }
        return cardAdditions;
    }

    /**
     * 处理灵装词条的至宝加成
     * @param attack
     * @param defense
     * @param strength
     * @param tenacity
     * @return
     */
    public Map<String,Integer> gainAdditions(Integer zhiBaoId,Integer attack,Integer defense,Integer strength,Integer tenacity){
        Map<String,Integer> map = new HashMap<>();
        //御器决
        if (zhiBaoId / 100 == XianJueTypeEnum.YU_QI_JUE.getValue()) {
            //攻
            map.put(String.valueOf(CardEquipmentAdditionEnum.ATTACK.getValue()),attack);
        }
        if (zhiBaoId / 100 == XianJueTypeEnum.KONG_BAO_SHU.getValue()) {
            //防
            map.put(String.valueOf(CardEquipmentAdditionEnum.DEFENSE.getValue()),defense);
        }
        //强韧
        map.put(String.valueOf(CardEquipmentAdditionEnum.STRENGTH.getValue()),strength);
        map.put(String.valueOf(CardEquipmentAdditionEnum.TENACITY.getValue()),tenacity);
        return map;
    }
}
