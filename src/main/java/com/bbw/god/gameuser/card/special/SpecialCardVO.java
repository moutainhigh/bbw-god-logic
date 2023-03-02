package com.bbw.god.gameuser.card.special;

import com.bbw.god.game.wanxianzhen.WanXianCard;
import lombok.Data;

import java.io.Serializable;

/**
 * @author：lwb
 * @date: 2020/12/2 14:33
 * @version: 1.0
 */
@Data
public class SpecialCardVO implements Serializable {
    private Integer id;
    private Integer skill0 = 0;
    private Integer skill5 = 0;
    private Integer skill10 = 0;
    private Integer attackSymbol = 0;// 攻击符箓
    private Integer defenceSymbol = 0;// 防御符箓
    private Integer isUseSkillScroll;// 是否使用技能卷轴

    public static SpecialCardVO instanceByWanXianCard(WanXianCard card){
        SpecialCardVO vo=new SpecialCardVO();
        vo.setId(card.getCardId());
        vo.setSkill0(card.getSkill0());
        vo.setSkill5(card.getSkill5());
        vo.setSkill10(card.getSkill10());
        vo.setAttackSymbol(card.getAttackSymbol());
        vo.setDefenceSymbol(card.getDefenceSymbol());
        vo.setIsUseSkillScroll(card.getIsUseSkillScroll());
        return vo;
    }
}
