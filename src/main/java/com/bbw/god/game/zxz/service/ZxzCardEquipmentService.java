package com.bbw.god.game.zxz.service;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.cfg.CfgLingZhuangEntryEntity;
import com.bbw.god.game.zxz.cfg.ZxzEntryTool;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.card.equipment.CardEquipmentService;
import com.bbw.god.gameuser.card.equipment.CfgXianJueTool;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.Enum.XianJueTypeEnum;
import com.bbw.god.gameuser.card.equipment.Enum.ZhiBaoEnum;
import com.bbw.god.gameuser.card.equipment.cfg.CardEquipmentAddition;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 诛仙阵卡牌装备工具类
 * @author: hzf
 * @create: 2022-09-29 19:16
 **/
@Service
public class ZxzCardEquipmentService {
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;

    @Autowired
    private CardEquipmentService cardEquipmentService;

    /**
     * 获取仙诀和至宝战斗加成
     *
     * @param allCardXianJues
     * @param allCardZhiBaos
     * @param cardIds
     * @return
     */
    public List<CardFightAddition> getEquipmentAdditions(List<CardXianJueRandomRule> allCardXianJues, List<CardZhiBaoRandomRule> allCardZhiBaos, List<Integer> cardIds) {

        List<CardFightAddition> additions = new ArrayList<>();
        for (Integer cardId : cardIds) {
            List<CardEquipmentAddition> attackAndHpAdditions = new ArrayList<>();
            //下标0 御器决， 1空报术
            List<CardEquipmentAddition>[] strengthAndTenacityAdditions = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
            List<Integer>[] cardEquipmentSkills = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
            //获取仙决加成
            List<CardXianJueRandomRule> cardXianJues = allCardXianJues.stream().filter(tmp -> tmp.getCardId().equals(cardId)).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(cardXianJues)) {
                for (CardXianJueRandomRule cardXianJue : cardXianJues) {
                    //仙决的基本加成
                    attackAndHpAdditions.addAll(cardXianJue.gainAdditions());
                    attackAndHpAdditions.addAll(CfgXianJueTool.getBaseXianJueAddition(cardXianJue.getXianJueType()));
                    if (XianJueTypeEnum.YU_QI_JUE.getValue() == cardXianJue.getXianJueType()) {
                        strengthAndTenacityAdditions[0] = cardXianJue.gainAdditions();
                        continue;
                    }
                    strengthAndTenacityAdditions[1] = cardXianJue.gainAdditions();

                }
            }
            //获取至宝加成和技能
            List<CardZhiBaoRandomRule> cardZhiBaos = allCardZhiBaos.stream().filter(tmp -> tmp.getCardId().equals(cardId)).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(cardZhiBaos)) {
                for (CardZhiBaoRandomRule cardZhiBao : cardZhiBaos) {
                    attackAndHpAdditions.addAll(cardZhiBao.gainAdditions());
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
            CardFightAddition addition = cardEquipmentService.handleCardFightAddition(cardId, attackAndHpAdditions, cardEquipmentSkills, strengthAndTenacityAdditions);
            additions.add(addition);
        }
        return additions;
    }
    
    /**
     * 获取卡牌装备战斗加成
     *
     * @param uid
     * @param regionId 区域id
     * @return
     */
    public List<CardFightAddition> getEquipmentAdditions(long uid, Integer regionId) {
        List<CardFightAddition> cardFightAdditions = new ArrayList<>();
        UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
        for (UserZxzCard zxzCard : userCardGroup.getCards()) {
            cardFightAdditions.add(zxzCard.getAddition());
        }
        return cardFightAdditions;
    }

    /**
     * 四圣挑战卡牌仙决加成
     * @param uid
     * @param challengeType
     * @return
     */
    public List<CardFightAddition> getFourSaintsEquipmentAdditions(long uid, Integer challengeType) {
        List<CardFightAddition> cardFightAdditions = new ArrayList<>();
        UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
        for (UserZxzCard zxzCard : userCardGroup.getCards()) {
            cardFightAdditions.add(zxzCard.getAddition());
        }
        return cardFightAdditions;
    }


}
