package com.bbw.god.game.transmigration.rd;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.transmigration.entity.TransmigrationCard;
import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮回世界主页信息
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationDefenderInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 守将属性 */
    private Integer type;
    /** 护符 */
    private List<Integer> runes;
    /** 守卫卡牌 */
    private List<RDGameUser.RDCard> cards;
    /** buff */
    private List<Integer> buffs;


    public void setCards(List<TransmigrationCard> defenderCards) {
        List<RDGameUser.RDCard> rdCards = defenderCards.stream().map(tmp -> {
            RDGameUser.RDCard rdCard = new RDGameUser.RDCard();
            rdCard.setBaseId(tmp.getId());
            rdCard.setLevel(tmp.getLv());
            rdCard.setHierarchy(tmp.getHv());
            rdCard.setSkill0(tmp.getSkills().get(0));
            rdCard.setSkill5(tmp.getSkills().get(1));
            rdCard.setSkill10(tmp.getSkills().get(2));
            boolean isUseSkillScroll = false;
            List<Integer> originalSkills = CardTool.getCardById(tmp.getId()).getSkills();
            for (int i = 0; i < tmp.getSkills().size(); i++) {
                if (tmp.getSkills().get(i).intValue() != originalSkills.get(i)){
                    isUseSkillScroll = true;
                    break;
                }
            }
            rdCard.setIsUseSkillScroll(isUseSkillScroll?1:0);
            return rdCard;
        }).collect(Collectors.toList());
        this.cards = rdCards;
    }


}
