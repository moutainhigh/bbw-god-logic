package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.UserZxzCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回关卡卡组
 * @author: hzf
 * @create: 2022-12-28 15:26K
 **/
@Data
public class RdDefenderCardGroup {
    /** 召唤师等级 */
    private Integer summonerLv;
    /** 召唤师血量 */
    private Integer summonerHp;
    /** 召唤师最大血量 */
    private Integer summonerMaxHp;
    /** 卡牌信息 */
    private List<RdZxzDefenderCard> cards = new ArrayList<>();
    /** 主角卡信息 */
    private RdZxzUserLeaderCard zxzUserLeaderCard;

    public static RdDefenderCardGroup getInstance(UserZxzFourSaintsCardGroupInfo userCardGroup,String nickname){
        RdDefenderCardGroup cardGroup = new RdDefenderCardGroup();
        cardGroup.setSummonerHp(userCardGroup.getHp());
        cardGroup.setSummonerMaxHp(userCardGroup.getMaxHp());
        cardGroup.setSummonerLv(userCardGroup.getUserLevel());
        List<RdDefenderCardGroup.RdZxzDefenderCard> rdUserCards = RdDefenderCardGroup.RdZxzDefenderCard.getInstance(userCardGroup.getCards());
        cardGroup.setCards(rdUserCards);
        if (null != userCardGroup.getZxzUserLeaderCard()) {
            cardGroup.setZxzUserLeaderCard(RdZxzUserLeaderCard.getInstance(userCardGroup.getZxzUserLeaderCard(),nickname));
        }
        return cardGroup;
    }
    public static RdDefenderCardGroup getInstance(UserZxzCardGroupInfo userCardGroup,String nickname){
        RdDefenderCardGroup cardGroup = new RdDefenderCardGroup();
        cardGroup.setSummonerHp(userCardGroup.getHp());
        cardGroup.setSummonerMaxHp(userCardGroup.getMaxHp());
        cardGroup.setSummonerLv(userCardGroup.getUserLevel());
        List<RdDefenderCardGroup.RdZxzDefenderCard> rdUserCards = RdDefenderCardGroup.RdZxzDefenderCard.getInstance(userCardGroup.getCards());
        cardGroup.setCards(rdUserCards);
        //如果有主角卡
        if (null != userCardGroup.getZxzUserLeaderCard()) {
            cardGroup.setZxzUserLeaderCard(RdZxzUserLeaderCard.getInstance(userCardGroup.getZxzUserLeaderCard(),nickname));
        }
        return cardGroup;
    }

    @Data
    public static class RdZxzDefenderCard{
        /** 卡牌id */
        private Integer cardId;
        /** 卡牌是否战败 */
        private boolean alive;
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


        public static List<RdZxzDefenderCard> getInstance(List<UserZxzCard> cards){
            List<RdZxzDefenderCard> rdCards = new ArrayList<>();
            for (UserZxzCard card : cards) {
                RdZxzDefenderCard rdCard = new RdZxzDefenderCard();
                rdCard.setCardId(card.getCardId());
                rdCard.setAlive(card.getAlive());
                rdCard.setLv(card.getLv());
                rdCard.setHv(card.getHv());
                rdCard.setAttackSymbol(card.getAttackSymbol());
                rdCard.setDefenceSymbol(card.getDefenceSymbol());
                rdCard.setSkills(card.getSkills());
                int isUseSkillScroll = card.ifUseSkillScroll(card.getSkills(),card.getCardId()) ? 1 : 0;
                rdCard.setIsUseSkillScroll(isUseSkillScroll);
                rdCard.setZhiBaos(RdZxzCardZhiBao.instance(card.getZhiBaos()));
                rdCard.setXianJues(RdZxzCardXianJue.instance(card.getXianJues()));
                rdCards.add(rdCard);
            }
            return rdCards;
        }

    }
}
