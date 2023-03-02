package com.bbw.god.game.config.card.equipment.randomrule;

import com.bbw.common.ListUtil;
import com.bbw.common.MapUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.gameuser.card.equipment.CfgXianJueTool;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.QualityEnum;
import com.bbw.god.gameuser.card.equipment.Enum.XianJueTypeEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 卡牌仙决基本规则
 * @author: hzf
 * @create: 2022-12-13 10:40
 **/
@Data
public class CardXianJueRandomRule implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 卡牌id */
    private Integer cardId;
    /** 装备类型 */
    private Integer xianJueType;
    /** 等级 */
    private Integer level = 0;
    /** 品质 */
    private Integer quality = QualityEnum.FAN_PIN.getValue();
    /** 星图进度 */
    private Integer starMapProgress = 0;
    /** 参悟值 加成类型=》加成值 */
    private Map<String, Integer> additions = new HashMap<>();

    @Override
    public String toString() {
        String SPLIT_CHAR = "@";
        String additionMaps = CfgCardEquipmentRandomRuleTool.convertWithStream(additions);
        return cardId + SPLIT_CHAR + xianJueType + SPLIT_CHAR
                + level + SPLIT_CHAR + quality + SPLIT_CHAR + starMapProgress+SPLIT_CHAR + additionMaps;

    }

    public static CardXianJueRandomRule getInstance(Integer cardId,CfgCardEquipmentRandomRuleEntity.CfgXianJue xianJue){
        CardXianJueRandomRule cardXianJue = new CardXianJueRandomRule();
        cardXianJue.setCardId(cardId);
        cardXianJue.setXianJueType(xianJue.getXianJueType());
        cardXianJue.setLevel(xianJue.getLevel());
        cardXianJue.setQuality(xianJue.getQuality());
        cardXianJue.setStarMapProgress(xianJue.getStarMapProgress());
        cardXianJue.setAdditions(xianJue.getAdditions());
        return cardXianJue;
    }

    /**
     * 构建卡牌仙决
     * @param cardIds
     * @return
     */
    public static List<CardXianJueRandomRule> instanceCardXianJue(List<Integer> cardIds){
        if (ListUtil.isEmpty(cardIds)) {
            return new ArrayList<>();
        }
        List<CardXianJueRandomRule> cardXianJues = new ArrayList<>();
        for (Integer cardId : cardIds) {
            List<CfgCardEquipmentRandomRuleEntity.CfgXianJue> xianJues = CfgCardEquipmentRandomRuleTool.getXianJues(FightTypeEnum.ZXZ.getValue());
            for (CfgCardEquipmentRandomRuleEntity.CfgXianJue xianJue : xianJues) {
                CardXianJueRandomRule cardXianJue = CardXianJueRandomRule.getInstance(cardId, xianJue);
                cardXianJues.add(cardXianJue);
            }
        }
        return cardXianJues;
    }

    /**
     * 将卡牌仙决转化成字符串
     * @param cardIds
     * @return
     */
    public static List<String> instances(List<Integer> cardIds) {
        List<String> cardXianJuesString = new ArrayList<>();
        List<CardXianJueRandomRule> cardXianJues = instanceCardXianJue(cardIds);
        for (CardXianJueRandomRule cardXianJue : cardXianJues) {
            String toString = cardXianJue.toString();
            cardXianJuesString.add(toString);
        }
        return cardXianJuesString;
    }
    /**
     * 获取仙决加成
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
     * 获取仙决加成
     * @param xianJueLv
     * @param quality
     * @param starMapProgress
     * @param comprehension
     * @return
     */
    public Map<String ,Integer> gainAdditions(Integer xianJueType,Integer xianJueLv,Integer quality,Integer starMapProgress,Integer comprehension){
        Map<String,Integer> map = new HashMap<>();
        //根据仙决等级、品质、星图进度处理攻防
        List<CardEquipmentAddition> cardEquipmentAdditions = CfgXianJueTool.getLevelAddition(xianJueLv, quality, starMapProgress);
        for (CardEquipmentAddition cardEquipmentAddition : cardEquipmentAdditions) {
            if (cardEquipmentAddition.getType().equals(xianJueType)) {
                map.put(String.valueOf(cardEquipmentAddition.getType()),cardEquipmentAddition.getValue());
            }
        }
        //参悟值加成
        map.put(String.valueOf(CardEquipmentAdditionEnum.STRENGTH_RATE.getValue()),comprehension);
        map.put(String.valueOf(CardEquipmentAdditionEnum.TENACITY_RATE.getValue()),comprehension);
        return map;
    }
}


