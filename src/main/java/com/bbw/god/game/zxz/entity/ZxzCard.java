package com.bbw.god.game.zxz.entity;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.zxz.constant.ZxzConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 关卡卡牌数据
 * @author: hzf
 * @create: 2022-09-14 16:43
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZxzCard implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 卡牌id */
    private Integer cardId;
    /** 等级 */
    private Integer lv;
    /** 阶数 */
    private Integer hv;
    /** 卡牌 */
    private List<Integer> skills;

    @Override
    public String toString() {
        String skillStr = "";
        for (Integer skill : skills) {
            if (skillStr.length() != 0) {
                skillStr += ",";
            }
            skillStr += skill;
        }
        return cardId + ZxzConstant.SPLIT_CHAR + lv + ZxzConstant.SPLIT_CHAR + hv + ZxzConstant.SPLIT_CHAR + skillStr;
    }
    /**
     * 判断是否使用技能卷轴
     * @param skills
     * @return
     */
    public boolean ifUseSkillScroll(List<Integer> skills,Integer cardId){
        CfgCardEntity cfgCard = CardTool.getCardById(cardId);
        return !cfgCard.getSkills().containsAll(skills);
    }

}
