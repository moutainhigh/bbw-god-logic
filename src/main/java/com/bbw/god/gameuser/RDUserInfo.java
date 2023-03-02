package com.bbw.god.gameuser;

import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCard.UserCardStrengthenInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月4日 下午5:13:52
 * 类说明
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RDUserInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guild = null;//行会名称 没有则是无
    private Integer level = null;
    private String nickname = null;
    private List<ShowCard> cards = null;//展示的卡牌  返回格式与登录时获取的相同
    private List<ItemInfo> infos = null;//各个排行
    private Integer head = null;
    private Integer headIcon = null;
    private Integer isMyFriend=0;
    private String shortServerName=null;

    public void setCards(List<UserCard> showCards) {
		this.cards = new ArrayList<>();
        for (UserCard card : showCards) {
			this.cards.add(ShowCard.instance(card));
        }
    }

    public void addItemInfo(ItemInfo info) {
        if (this.infos == null) {
			this.infos = new ArrayList<RDUserInfo.ItemInfo>();
        }
		this.infos.add(info);
    }

    //子信息
    @Data
    static class ItemInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer type;//类型
        private Long num;//数量、排名
        private String content;//主要内容 如胜率  100%

        public static ItemInfo instance(UserInfoEnum typeEnum, Long num, String content) {
            ItemInfo info = new ItemInfo();
            info.setContent(content);
            info.setNum(num == null ? 0L : num);
            info.setType(typeEnum.getType());
            return info;
        }

        public static ItemInfo instance(UserInfoEnum typeEnum, Integer num, String content) {
            Long val = num == null ? 0L : num.intValue();
            return instance(typeEnum, val, content);
        }
    }

    @Data
    static class ShowCard implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer cardId;//卡id
        private Integer level = 0;//卡等级
        private Integer hierarchy = 0;//卡阶级
        private Integer skill0;
        private Integer skill5;
        private Integer skill10;
        private Integer attackSymbol = 0;// 攻击符箓
        private Integer defenceSymbol = 0;// 防御符箓
        private Integer isUseSkillScroll;// 是否使用技能卷轴

        public static ShowCard instance(UserCard card) {
            ShowCard showCard = new ShowCard();
            showCard.setCardId(card.getBaseId());
            showCard.setHierarchy(card.getHierarchy());
            showCard.setLevel(card.getLevel());
            showCard.setSkill0(card.gainSkill0());
            showCard.setSkill5(card.gainSkill5());
            showCard.setSkill10(card.gainSkill10());
            showCard.setDefenceSymbol(card.gainDefenceSymbol());
            showCard.setAttackSymbol(card.gainAttackSymbol());
            showCard.setIsUseSkillScroll(card.ifUseSkillScroll() ? 1 : 0);
            return showCard;
        }
    }
}
