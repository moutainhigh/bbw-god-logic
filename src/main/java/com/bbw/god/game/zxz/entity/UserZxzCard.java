package com.bbw.god.game.zxz.entity;

import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家卡牌数据
 * @author: hzf
 * @create: 2022-09-14 20:10
 **/
@Data
public class UserZxzCard extends ZxzCard implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 卡牌是否活着（战败） */
    private Boolean alive = true;
    /** 攻击符箓 */
    private Integer attackSymbol;
    /** 防御符箓 */
    private Integer defenceSymbol;
    @Deprecated
    private List<Long> ZhiBaoId;
    @Deprecated
    private List<Long> xianJueId;
    /** 至宝 */
    private List<UserZxzCardZhiBao> zhiBaos;
    /** 仙决 */
    private List<UserZxzCardXianJue> xianJues;
    /** 增加属性 */
    private CardFightAddition addition;

    /**
     * 获取攻击符篆
     * @param attackSymbol
     * @return
     */
    private Integer gainAttackSymbol(Integer attackSymbol){
        if (0 == attackSymbol) {
            return null;
        }
        return attackSymbol;
    }

    /**
     * 获取防御符篆
     * @param defenceSymbol
     * @return
     */
    private Integer gainDefenceSymbol(Integer defenceSymbol){
        if (0 == defenceSymbol) {
            return null;
        }
        return defenceSymbol;
    }
    /**
     * 获取卡牌初始化
     * @param cards
     * @return
     */
    public static List<CCardParam> gainCCardParam(List<UserZxzCard> cards){
        List<CCardParam> cardParams = new ArrayList<>();
        for (UserZxzCard card : cards) {
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0, card.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5, card.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10, card.getSkills().get(2));
            strengthenInfo.setAttackSymbol(card.getAttackSymbol());
            strengthenInfo.setDefenceSymbol(card.getDefenceSymbol());
            CCardParam cCardParam = CCardParam.init(card.getCardId(), card.getLv(), card.getHv(), strengthenInfo);
            cCardParam.setAlive(card.getAlive());
            cardParams.add(cCardParam);
        }
        return cardParams;
    }

    /**
     * 构建 List<UserZxzCard> 实例
     * @param userCards
     * @return
     */
    public static List<UserZxzCard> getInstance(List<UserCard> userCards){
        List<UserZxzCard> userZxzCards = new ArrayList<>();
        for (UserCard userCard : userCards) {
            UserZxzCard userZxzCard = new UserZxzCard();
            userZxzCard.setCardId(userCard.getBaseId());
            userZxzCard.setAlive(true);
            userZxzCard.setLv(userCard.getLevel());
            userZxzCard.setHv(userCard.getHierarchy());
            Integer attackSymbol = userZxzCard.gainAttackSymbol(userCard.gainAttackSymbol());
            userZxzCard.setAttackSymbol(attackSymbol);
            Integer defenceSymbol = userZxzCard.gainDefenceSymbol(userCard.gainDefenceSymbol());
            userZxzCard.setDefenceSymbol(defenceSymbol);
            userZxzCard.setSkills(userCard.gainSkillsByLv());
            userZxzCards.add(userZxzCard);
        }
        return userZxzCards;
    }






}
