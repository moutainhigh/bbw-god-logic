package com.bbw.god.game.zxz.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回榜单的卡组
 * @author: hzf
 * @create: 2022-10-11 09:19
 **/
@Data
public class RdUserRankCardGroup extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 区域Id */
    private Integer regionId;
    /** 卡牌数据 */
    private List<RdUserPassRegionCard> cards = new ArrayList<>();
    /** 符图数据 */
    private List<ZxzFuTu> fuTus = new ArrayList<>();
    /** 符册名称 */
    private String fuCeName = "";
    /** 词条 */
    private List<ZxzEntry> entries = new ArrayList<>();
    /** 区域等级 */
    private Integer regionLv = 0;


    public static RdUserRankCardGroup getInstance(UserPassRegionCardGroupInfo cardGroup,Integer regionId){
        RdUserRankCardGroup rd = new RdUserRankCardGroup();
        if (null == cardGroup) {
            Integer difficulty = ZxzTool.getDifficulty(regionId);
            rd.setDifficulty(difficulty);
            rd.setRegionId(regionId);
            return  rd;
        }

        rd.setDifficulty(cardGroup.getDifficulty());
        rd.setRegionId(cardGroup.getRegionId());
        List<ZxzFuTu> runes = ListUtil.isEmpty(cardGroup.getRunes()) ? new ArrayList<>():cardGroup.getRunes();
        rd.setFuTus(runes);
        rd.setFuCeName(cardGroup.getFuceName());
        rd.setRegionLv(cardGroup.getRegionLv());
        rd.setEntries(cardGroup.gainEntrys());
        rd.setCards(rd.gainUserPassRegionCard(cardGroup.getCards()));
        return rd;
    }

    /**
     * 获取通关卡组信息
     * @param cards
     * @return
     */
    public List<RdUserPassRegionCard> gainUserPassRegionCard(List<UserPassRegionCard> cards){
        List<RdUserPassRegionCard> rdCards = new ArrayList<>();
        for (UserPassRegionCard card : cards) {
            RdUserPassRegionCard rdPassRegionCard = RdUserPassRegionCard.getInstance(card);
            rdCards.add(rdPassRegionCard);
        }
        return rdCards;
    }

    @Data
    public static class RdUserPassRegionCard{
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

        public static RdUserPassRegionCard getInstance(UserPassRegionCard card){
            RdUserPassRegionCard rdPassRegionCard = new RdUserPassRegionCard();
            rdPassRegionCard.setCardId(card.getCardId());
            rdPassRegionCard.setLv(card.getLv());
            rdPassRegionCard.setHv(card.getHv());
            rdPassRegionCard.setSkills(card.getSkills());
            rdPassRegionCard.setAttackSymbol(card.getAttackSymbol());
            rdPassRegionCard.setDefenceSymbol(card.getDefenceSymbol());
            List<RdZxzCardZhiBao> rdZxzCardZhiBaos = RdZxzCardZhiBao.instance(card.getZhiBaos());
            rdPassRegionCard.setZhiBaos(rdZxzCardZhiBaos);
            List<RdZxzCardXianJue> rdZxzCardXianJues = RdZxzCardXianJue.instance(card.getXianJues());
            rdPassRegionCard.setXianJues(rdZxzCardXianJues);
            int isUseSkillScroll = card.ifUseSkillScroll(card.getSkills(),card.getCardId()) ? 1:0;
            rdPassRegionCard.setIsUseSkillScroll(isUseSkillScroll);
            return rdPassRegionCard;
        }

    }

}
