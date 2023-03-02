package com.bbw.god.game.wanxianzhen;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/5/8 15:38
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class WanXianCard implements Serializable {
    private static final long serialVersionUID = 6842950078278252120L;
    private Integer cardId;
    private Integer lv = 20;
    private Integer hv = 10;
    private Integer skill0;
    private Integer skill5;
    private Integer skill10;
    private List<Integer> extraSkills = new ArrayList<>();
    private Integer attackSymbol = 0;// 攻击符箓
    private Integer defenceSymbol = 0;// 防御符箓
    private Integer isUseSkillScroll;// 是否使用技能卷轴
    private Integer initAtk = 0;//有值则用该值 无则用规则计算
    private Integer initHp = 0;//有值则用该值 无则用规则计算
    private Integer star;//星级
    private TypeEnum type;//属性
    private Integer sex;
    private Integer addZhsHp = 0;
    private Integer fashion;
    /**
     * 装备
     */
    private UserLeaderEquipment[] equips = null;
    /**
     * 宠物
     */
    private int[] beasts = null;
    private String cardName;

    public static WanXianCard instance(int cardId){
        WanXianCard card=new WanXianCard();
        card.setCardId(cardId);
        return card;
    }
    public static WanXianCard instance(int cardId,CfgCardEntity cardCfg){
        WanXianCard card=new WanXianCard();
        card.setCardId(cardId);
        card.setSkill0(cardCfg.getZeroSkill());
        card.setSkill5(cardCfg.getFiveSkill());
        card.setSkill10(cardCfg.getTenSkill());
        card.setIsUseSkillScroll(0);
        return card;
    }

    /**
     * 更细卡牌信息
     * @param userCard
     */
    public void updateCardInfo(UserCard userCard){
        cardId=userCard.getBaseId();
        skill0=userCard.gainSkill0();
        skill5=userCard.gainSkill5();
        skill10=userCard.gainSkill10();
        attackSymbol=userCard.gainAttackSymbol();
        defenceSymbol=userCard.gainDefenceSymbol();
        isUseSkillScroll=userCard.ifUseSkillScroll()?1:0;
    }

    /**
     * 更新主角卡牌信息
     * @param leaderCard
     * @param extraSkills 额外的神兽技能
     */
    public void updateCardInfo(UserLeaderCard leaderCard, List<Integer> extraSkills) {
        cardId = leaderCard.getBaseId();
        int[] skills = leaderCard.currentSkills();
        skill0 = skills[0];
        skill5 = skills[1];
        skill10 = skills[2];
        if (ListUtil.isNotEmpty(extraSkills)) {
            this.extraSkills = extraSkills;
        } else {
            this.extraSkills = new ArrayList<>();
        }

        attackSymbol = 0;
        defenceSymbol = 0;
        isUseSkillScroll = 1;
        sex = leaderCard.getSex();
        type = TypeEnum.fromValue(leaderCard.getProperty());
        star = leaderCard.getStar();
        fashion = leaderCard.getFashion();
    }

    public CfgCardEntity gainCard() {
        return CardTool.getCardById(cardId);
    }

    public boolean ifSpecial(){
        return isUseSkillScroll !=null &&(isUseSkillScroll >0||attackSymbol>0||defenceSymbol>0);
    }

    public String buildSkillAndSymbolStr(){
        String skillStr="";
        if (skill0!=null && skill0>0){
            skillStr+=skill0+",";
        }else {
            skillStr+="0,";
        }
        if (skill5!=null && skill5>0){
            skillStr+=skill5+",";
        }else {
            skillStr+="0,";
        }
        if (skill10!=null && skill10>0){
            skillStr+=skill10+",";
        }else {
            skillStr+="0,";
        }
        if (attackSymbol!=null && attackSymbol>0){
            skillStr+=attackSymbol+",";
        } else {
            skillStr += "0,";
        }
        if (defenceSymbol != null && defenceSymbol > 0) {
            skillStr += defenceSymbol + ",";
        } else {
            skillStr += "0;";
        }
        return skillStr;
    }

    public Integer getFashion() {
        return fashion == null ? TreasureEnum.FASHION_FaSFS.getValue() : fashion;
    }
}
