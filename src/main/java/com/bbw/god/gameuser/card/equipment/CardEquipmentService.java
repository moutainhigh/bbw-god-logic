package com.bbw.god.gameuser.card.equipment;

import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.XianJueTypeEnum;
import com.bbw.god.gameuser.card.equipment.Enum.ZhiBaoEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡牌装备工具类
 *
 * @author: huanghb
 * @date:   2022/9/30 10:11
 */
@Service
public class CardEquipmentService {
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    /** 卡牌 **/
    public static Map<Integer, CardFightAddition.SkillAddition> SKILL_MAX_RATE = new HashMap<Integer, CardFightAddition.SkillAddition>() {
        private static final long serialVersionUID = -889583152152861562L;

        {
            put(10101, new CardFightAddition.SkillAddition(0.6,1.0));
            put(10102, new CardFightAddition.SkillAddition(0.6,1.2));
            put(10201, new CardFightAddition.SkillAddition(0.4,0.0));
            put(10202, new CardFightAddition.SkillAddition(0.5,0.0));

            put(11101, new CardFightAddition.SkillAddition(0.6,0.6));
            put(11102, new CardFightAddition.SkillAddition(0.6,0.6));
            put(11201, new CardFightAddition.SkillAddition(0.3,0.0));
            put(11202, new CardFightAddition.SkillAddition(0.4,0.0));
        }
    };


    /**
     * 获取仙诀和至宝战斗加成
     *
     * @param uid
     * @return
     */
    public List<CardFightAddition> getEquipmentAdditions(long uid, List<Integer> cardIds) {
        //根据卡牌获得所有卡牌的仙诀信息
        List<UserCardXianJue> allCardXianJues = userCardXianJueService.getUserCardXianJues(uid, cardIds);
        //根据卡牌获得所有卡牌的至宝信息
        List<UserCardZhiBao> allCardZhiBaos = userCardZhiBaoService.getUserCardZhiBaos(uid, cardIds);
        List<CardFightAddition> additions = new ArrayList<>();
        for (Integer cardId : cardIds) {
            List<CardEquipmentAddition> attackAndHpAdditions = new ArrayList<>();
            //下标0 御器决， 1空报术
            List<CardEquipmentAddition>[] strengthAndTenacityAdditions = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
            List<Integer>[] cardEquipmentSkills = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
            //获取仙决加成
            List<UserCardXianJue> cardXianJues = allCardXianJues.stream().filter(tmp -> tmp.ifPutOnCard(cardId)).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(cardXianJues)) {
                for (UserCardXianJue cardXianJue : cardXianJues) {
                    attackAndHpAdditions.addAll(userCardXianJueService.getAdditions(cardXianJue));
                    if (XianJueTypeEnum.YU_QI_JUE.getValue() == cardXianJue.getXianJueType()) {
                        strengthAndTenacityAdditions[0] = cardXianJue.gainAdditions();
                        continue;
                    }
                    strengthAndTenacityAdditions[1] = cardXianJue.gainAdditions();

                }
            }
            //获取至宝加成和技能
            List<UserCardZhiBao> cardZhiBaos = allCardZhiBaos.stream().filter(tmp -> tmp.ifPutOnCard(cardId)).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(cardZhiBaos)) {
                for (UserCardZhiBao cardZhiBao : cardZhiBaos) {
                    attackAndHpAdditions.addAll(userCardZhiBaoService.getAdditions(cardZhiBao));
                    if (null == cardZhiBao.getSkillGroup()) {
                        continue;
                    }
                    int zhiBaoType = cardZhiBao.getZhiBaoId() / 100;
                    if (ZhiBaoEnum.FA_QI.getValue() == zhiBaoType) {
                        strengthAndTenacityAdditions[0].addAll(cardZhiBao.gainAdditions());
                        cardEquipmentSkills[0] = Arrays.stream(cardZhiBao.getSkillGroup()).collect(Collectors.toList());
                        continue;
                    }
                    strengthAndTenacityAdditions[1].addAll(cardZhiBao.gainAdditions());
                    cardEquipmentSkills[1] = Arrays.stream(cardZhiBao.getSkillGroup()).collect(Collectors.toList());
                }
            }
            CardFightAddition addition = handleCardFightAddition(cardId, attackAndHpAdditions, cardEquipmentSkills, strengthAndTenacityAdditions);
            additions.add(addition);
        }
        return additions;
    }

    /**
     * 处理卡牌加成
     * @param cardId
     * @param attackAndHpAdditions
     * @param cardEquipmentSkills
     * @param strengthAndTenacityAdditions
     * @return
     */
    public CardFightAddition handleCardFightAddition(Integer cardId,List<CardEquipmentAddition> attackAndHpAdditions, List<Integer>[] cardEquipmentSkills, List<CardEquipmentAddition>[] strengthAndTenacityAdditions){
        //获取各加成值
        int attack = 0;
        int defense = 0;
        if (ListUtil.isNotEmpty(attackAndHpAdditions)) {
            Map<Integer, Integer> additionMap = attackAndHpAdditions.stream().collect(Collectors.groupingBy(CardEquipmentAddition::getType, Collectors.summingInt(CardEquipmentAddition::getValue)));
            attack = additionMap.getOrDefault(CardEquipmentAdditionEnum.ATTACK.getValue(), 0);
            defense = additionMap.getOrDefault(CardEquipmentAdditionEnum.DEFENSE.getValue(), 0);
        }
        CardFightAddition addition = new CardFightAddition();
        addition.setCardId(cardId);
        addition.setAttack(attack);
        addition.setDefence(defense);

        // 技能加成处理
        for (int i = 0; i < cardEquipmentSkills.length; i++) {
            List<Integer> skills = cardEquipmentSkills[i];
            if (ListUtil.isEmpty(skills)) {
                continue;
            }
            int strengthRate = 0;
            int tenacityRate = 0;
            int strength = 0;
            int tenacity = 0;
            List<CardEquipmentAddition> strengthAndTenacityAddition = strengthAndTenacityAdditions[i];
            if (ListUtil.isNotEmpty(strengthAndTenacityAddition)) {
                Map<Integer, Integer> additionMap = strengthAndTenacityAddition.stream().collect(Collectors.groupingBy(CardEquipmentAddition::getType, Collectors.summingInt(CardEquipmentAddition::getValue)));
                strengthRate = additionMap.getOrDefault(CardEquipmentAdditionEnum.STRENGTH_RATE.getValue(), 0);
                tenacityRate = additionMap.getOrDefault(CardEquipmentAdditionEnum.TENACITY_RATE.getValue(), 0);
                strength = additionMap.getOrDefault(CardEquipmentAdditionEnum.STRENGTH.getValue(), 0);
                tenacity = additionMap.getOrDefault(CardEquipmentAdditionEnum.TENACITY.getValue(), 0);
            }

            for (Integer skill : skills) {
                addition.addSkillAddition(skill, strengthRate, tenacityRate, strength, tenacity);
            }
        }
        return addition;
    }

}
