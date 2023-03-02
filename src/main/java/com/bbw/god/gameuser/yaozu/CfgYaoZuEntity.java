package com.bbw.god.gameuser.yaozu;

import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明：妖族来犯配置
 *
 * @author fzj
 * @date 2021/9/6 11:57
 */
@Data
public class CfgYaoZuEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** id */
    private Integer yaoZuId;
    /** 类型 100 野猪妖 200 狗大仙 300 琵琶精 400 稚鸡精 500 妖狐仙*/
    private Integer yaoZuType;
    /** 名称 */
    private String name;
    /** 属性 10-金 20木 30水 40火 50土*/
    private Integer type;
    /** 召唤师等级 */
    private Integer aiLv;
    /** 卡牌阶数 */
    private Integer cardHv;
    /** 卡牌等级 */
    private Integer cardLv;
    /** 护符id */
    private List<Integer> runes;
    /** 妖族卡组 */
    private List<CardParam> yaoZuCards;

    @Override
    public Serializable getId() {
        return this.getYaoZuId();
    }

    @Override
    public int getSortId() {
        return this.getYaoZuId();
    }

    @Data
    public static class CardParam implements Serializable{
        private Integer id;
        private Integer skill0;
        private Integer skill5;
        private Integer skill10;
    }

    /**
     * 根据妖族卡组id获取完整卡组
     * @param yaoZuInfo
     * @return
     */
    public List<RDFightsInfo.RDFightCard> cardsInstance(CfgYaoZuEntity yaoZuInfo){
        List<RDFightsInfo.RDFightCard> yaoZuCards = new ArrayList<>();
        List<CfgYaoZuEntity.CardParam> yaoZuCardGroup = yaoZuInfo.getYaoZuCards();
        for (CfgYaoZuEntity.CardParam yaoZuCard : yaoZuCardGroup) {
            RDFightsInfo.RDFightCard yaoZuFightCard = new RDFightsInfo.RDFightCard(yaoZuCard.getId(), yaoZuInfo.getCardLv(), yaoZuInfo.getCardHv());
            yaoZuFightCard.setIsUseSkillScroll(1);
            yaoZuFightCard.setSkill0(yaoZuCard.getSkill0());
            yaoZuFightCard.setSkill5(yaoZuCard.getSkill5());
            yaoZuFightCard.setSkill10(yaoZuCard.getSkill10());
            yaoZuCards.add(yaoZuFightCard);
        }
        return yaoZuCards;
    }

}
