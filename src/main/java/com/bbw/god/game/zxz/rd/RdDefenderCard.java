package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.rd.foursaints.RdFourSaintsEnemyRegion;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 返回关卡卡牌
 * @author: hzf
 * @create: 2023-01-06 10:55
 **/
@Data
public class RdDefenderCard implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 卡牌id */
    private Integer cardId;
    /** 等级 */
    private Integer lv;
    /** 阶数 */
    private Integer hv;
    /** 卡牌 */
    private List<Integer> skills;
    /** 攻击符箓 */
    private Integer attackSymbol;
    /** 防御符箓 */
    private Integer defenceSymbol;
    /** 至宝 */
    private List<RdZxzCardZhiBao> zhiBaos;
    /** 仙决 */
    private List<RdZxzCardXianJue> xianJues;
    /** 是否使用技能卷轴*/
    private Integer isUseSkillScroll;

    public static RdDefenderCard getInstance(ZxzCard card, List<RdZxzCardZhiBao> zhiBaos, List<RdZxzCardXianJue> xianJues){
        RdDefenderCard rdCard = new RdDefenderCard();
        rdCard.setCardId(card.getCardId());
        rdCard.setLv(card.getLv());
        rdCard.setHv(card.getHv());
        rdCard.setAttackSymbol(0);
        rdCard.setDefenceSymbol(0);
        rdCard.setSkills(card.getSkills());
        int isUseSkillScroll = card.ifUseSkillScroll(card.getSkills(),card.getCardId()) ? 1 : 0;
        rdCard.setIsUseSkillScroll(isUseSkillScroll);
        rdCard.setZhiBaos(zhiBaos);
        rdCard.setXianJues(xianJues);
        return rdCard;
    }
}
