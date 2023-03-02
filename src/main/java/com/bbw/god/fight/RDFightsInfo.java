package com.bbw.god.fight;

import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFightsInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nickname = null;// 对方召唤师昵称
	private Integer head = null;// 对方召唤师头像
	private Integer headIcon = TreasureEnum.HEAD_ICON_Normal.getValue();// 对方召唤师头像
	private Integer level = null;// 对方召唤师等级
	private Long opponentId = null;//对手ID或野怪ID
	private Long cardFromUid = -1l;//来自哪个玩家的卡组（练兵时才会出现，练兵时对手ID是-1，但卡组可能来自某个玩家）
	private List<RDFightCard> cards = null;// 对手卡牌
	private Integer awardkey = -1;//打野怪宝箱奖励（非打野怪默认-1）
	private Integer blood = null;
	private Integer cityLevel = null;//城市等级 攻城时需要用来编排王者卡位置
	private Integer cityBaseId = null;
	private Integer cityHierarchy = null;
	private Integer ygType = null;// 野怪类型，参考YeGuaiEnum
	private Integer yeDEventType = null;// 野地事件类型，参考YdEventEnum
	private Integer cityBuff=0;

	public RDFightsInfo() {
	}

	public RDFightsInfo(int level, List<UserCard> cards) {
		this.level = level;
		this.cards = new ArrayList<>();
		for (UserCard card : cards) {
			this.cards.add(new RDFightCard(card));
		}
	}

	public RDFightsInfo(int level, List<UserCard> cards,int awardKey) {
		this.level = level;
		this.cards=new ArrayList<>();
		for (UserCard card:cards){
			this.cards.add(new RDFightCard(card));
		}
		this.awardkey = awardKey;
	}

	public RDFightsInfo(int level, List<RDFightCard> cards,int awardKey,int ygType) {
		this.level = level;
		this.cards=cards;
		this.awardkey = awardKey;
		this.ygType = ygType;
	}
	
	public RDFightsInfo(String name,int head,int level, List<UserCard> cards) {
		this.nickname= name;
		this.head = head;
		this.level = level;
		this.cards=new ArrayList<>();
		for (UserCard card:cards){
			this.cards.add(new RDFightCard(card));
		}
	}

	public static RDFightsInfo instance(GameUser gu, List<UserCard> fightCards){
		RDFightsInfo info=new RDFightsInfo();
		info.nickname= gu.getRoleInfo().getNickname();
		info.head = gu.getRoleInfo().getHead();
		info.level = gu.getLevel();
		info.cards=new ArrayList<>();
		info.setHeadIcon(gu.getRoleInfo().getHeadIcon());
		for (UserCard card:fightCards){
			info.cards.add(new RDFightCard(card));
		}
		info.setCardFromUid(gu.getId());
		return info;
	}
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDFightCard implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer baseId = null;
		private Integer level = null;
		private Integer hierarchy = null;
		private Integer isUseSkillScroll = 0;
		private Integer skill0;
		private Integer skill5;
		private Integer skill10;
		private Integer attackSymbol;// 攻击符箓
		private Integer defenceSymbol;// 防御符箓
		private UserCard.UserCardStrengthenInfo strengthenInfo;

		public RDFightCard(Integer baseId, Integer level, Integer hierarchy) {
			this.baseId = baseId;
			this.level = level;
			this.hierarchy = hierarchy;
			CfgCardEntity cc = CardTool.getCardById(baseId);
			this.skill0=cc.getZeroSkill();
			this.skill5=cc.getFiveSkill();
			this.skill10=cc.getTenSkill();
		}

		public RDFightCard(UserCard card){
			this.baseId=card.getBaseId();
			this.level=card.getLevel();
			this.hierarchy=card.getHierarchy();
			if (card.getStrengthenInfo()!=null){
				this.strengthenInfo=card.getStrengthenInfo();
			}
			isUseSkillScroll=card.ifUseSkillScroll()?1:0;
			this.setSkill0(card.gainSkill0());
			this.setSkill5(card.gainSkill5());
			this.setSkill10(card.gainSkill10());
		}

		public static RDFightCard instance(CCardParam cardParam){
			RDFightCard card = new RDFightCard(cardParam.getId(), cardParam.getLv(), cardParam.getHv());
			card.setStrengthenInfo(card.getStrengthenInfo());
			card.setIsUseSkillScroll(cardParam.getIsUseSkillScroll());
			card.setSkill0(cardParam.getSkills().get(0));
			card.setSkill5(cardParam.getSkills().get(1));
			card.setSkill10(cardParam.getSkills().get(2));
			return card;
		}
		public int gainAttackSymbol() {

			if (strengthenInfo.gainAttackSymbol() != null){
				attackSymbol=strengthenInfo.gainAttackSymbol();
			}
			if (strengthenInfo.gainAttackSymbol() != null){
				attackSymbol=strengthenInfo.gainAttackSymbol();
			}

			if (this.strengthenInfo != null && this.strengthenInfo.gainAttackSymbol() != null) {
				return this.strengthenInfo.gainAttackSymbol();
			}
			return 0;
		}
		public int gainDefenceSymbol() {
			if (this.strengthenInfo != null && this.strengthenInfo.gainDefenceSymbol() != null) {
				return this.strengthenInfo.gainDefenceSymbol();
			}
			return 0;
		}

	}

}
