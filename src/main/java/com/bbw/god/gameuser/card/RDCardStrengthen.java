package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.ZxzUserLeaderCard;
import com.bbw.god.game.zxz.rd.RdZxzCardXianJue;
import com.bbw.god.game.zxz.rd.RdZxzCardZhiBao;
import com.bbw.god.gameuser.card.equipment.rd.RdCardXianJueInfo;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 卡牌强化（炼体、修身）
 * 
 * @author suhq
 * @date 2019-10-14 14:27:49
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDCardStrengthen extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer cardId;
	private Integer skill0;
	private Integer skill5;
	private Integer skill10;
	private Integer attackSymbol = 0;// 攻击符箓
	private Integer defenceSymbol = 0;// 防御符箓
	private Integer isUseSkillScroll = 0;// 是否使用技能卷轴
	private Integer sex;
	private Integer fashion;
	private Integer property;
	private Integer star;
	private Integer level = null;
	private Integer hierarchy = null;
	private Integer hv;
	private Integer lv = null;
	private Integer atk;//总攻击
	private Integer hp;//总防御
	private List<RdCardZhiBao> zhiBaos;
	private List<RdCardXianJueInfo> xianJues;
	/**
	 * 装备
	 */
	private UserLeaderEquipment[] equips = null;
	/**
	 * 宠物
	 */
	private int[] beasts = null;
	private String cardName = null;

	public static RDCardStrengthen getInstance(UserLeaderCard leaderCard){
		RDCardStrengthen rd = new RDCardStrengthen();
		rd.setCardId(leaderCard.getBaseId());
		rd.setSkill0(leaderCard.currentSkills()[0]);
		rd.setSkill5(leaderCard.currentSkills()[1]);
		rd.setSkill10(leaderCard.currentSkills()[2]);
		rd.setProperty(leaderCard.getProperty());
		rd.setSex(leaderCard.getSex());
		rd.setFashion(leaderCard.getFashion());
		rd.setStar(leaderCard.getStar());
		rd.setLv(leaderCard.getLv());
		return rd;
	}

	public static RDCardStrengthen getInstance(WanXianCard wanXianCard){
		RDCardStrengthen rd=new RDCardStrengthen();
		rd.setCardId(wanXianCard.getCardId());
		rd.setSkill0(wanXianCard.getSkill0());
		rd.setSkill5(wanXianCard.getSkill5());
		rd.setSkill10(wanXianCard.getSkill10());
		if (wanXianCard.getCardId()== CardEnum.LEADER_CARD.getCardId()) {
			rd.setProperty(wanXianCard.getType().getValue());
			rd.setSex(wanXianCard.getSex());
			rd.setFashion(wanXianCard.getFashion());
			rd.setStar(wanXianCard.getStar());
			rd.setCardName(wanXianCard.getCardName());
			rd.setEquips(wanXianCard.getEquips());
			rd.setBeasts(wanXianCard.getBeasts());
			rd.setHp(wanXianCard.getInitHp());
			rd.setAtk(wanXianCard.getInitAtk());
		}
		rd.setLv(wanXianCard.getLv());
		rd.setHv(wanXianCard.getHv());
		rd.setDefenceSymbol(wanXianCard.getDefenceSymbol());
		rd.setAttackSymbol(wanXianCard.getAttackSymbol());
		rd.setIsUseSkillScroll(wanXianCard.getIsUseSkillScroll());
		return rd;
	}
	public static RDCardStrengthen getInstance(ZxzUserLeaderCard zxzUserLeaderCard,String nickname){
		RDCardStrengthen rd = new RDCardStrengthen();
		rd.setCardId(zxzUserLeaderCard.getCardId());
		rd.setSkill0(zxzUserLeaderCard.getSkills().get(0));
		rd.setSkill5(zxzUserLeaderCard.getSkills().get(1));
		rd.setSkill10(zxzUserLeaderCard.getSkills().get(2));
		rd.setProperty(zxzUserLeaderCard.getProperty());
		rd.setSex(zxzUserLeaderCard.getSex());
		rd.setFashion(zxzUserLeaderCard.getFashion());
		rd.setStar(zxzUserLeaderCard.getStar());
		rd.setLv(zxzUserLeaderCard.getLv());
		rd.setCardName(nickname);
		rd.setEquips(zxzUserLeaderCard.getEquips());
		rd.setBeasts(zxzUserLeaderCard.getBeasts());
		rd.setHierarchy(zxzUserLeaderCard.getHv());
		rd.setLevel(zxzUserLeaderCard.getLv());
		rd.setAtk(zxzUserLeaderCard.getAtk());
		rd.setHp(zxzUserLeaderCard.getHp());
		return rd;
	}
	public static RDCardStrengthen getInstance(UserZxzCard uZxzCard){
		RDCardStrengthen rd = new RDCardStrengthen();
		rd.setCardId(uZxzCard.getCardId());
		rd.setSkill0(uZxzCard.getSkills().get(0));
		rd.setSkill5(uZxzCard.getSkills().get(1));
		rd.setSkill10(uZxzCard.getSkills().get(2));
		rd.setAttackSymbol(uZxzCard.getAttackSymbol());
		rd.setDefenceSymbol(uZxzCard.getDefenceSymbol());
		int isUseSkillScroll =  uZxzCard.ifUseSkillScroll(uZxzCard.getSkills(), uZxzCard.getCardId()) ? 1:0;
		rd.setIsUseSkillScroll(isUseSkillScroll);
		rd.setZhiBaos(RdZxzCardZhiBao.gainRdCardZhiBaos(uZxzCard.getZhiBaos()));
		rd.setXianJues(RdZxzCardXianJue.gainRdCardXianJues(uZxzCard.getXianJues()));
		return rd;
	}
	public static RDCardStrengthen getInstance(ZxzCard zxzCard){
		RDCardStrengthen rd = new RDCardStrengthen();
		rd.setCardId(zxzCard.getCardId());
		rd.setSkill0(zxzCard.getSkills().get(0));
		rd.setSkill5(zxzCard.getSkills().get(1));
		rd.setSkill10(zxzCard.getSkills().get(2));
		rd.setAttackSymbol(0);
		rd.setDefenceSymbol(0);
		int isUseSkillScroll =  zxzCard.ifUseSkillScroll(zxzCard.getSkills(),zxzCard.getCardId()) ? 1:0;
		rd.setIsUseSkillScroll(isUseSkillScroll);
		return rd;
	}
	
	public static RDCardStrengthen getInstance(UserCard uc){
		RDCardStrengthen rd=new RDCardStrengthen();
		rd.setStrengthenInfo(uc);
		rd.setHv(uc.getHierarchy());
		rd.setLv(uc.getLevel());
		return rd;
	}

	public void setStrengthenInfo(UserCard uc) {
		this.cardId = uc.getBaseId();
		this.skill0 = uc.gainSkill0();
		this.skill5 = uc.gainSkill5();
		this.skill10 = uc.gainSkill10();
		this.attackSymbol = uc.gainAttackSymbol();
		this.defenceSymbol = uc.gainDefenceSymbol();
		this.isUseSkillScroll = uc.ifUseSkillScroll() ? 1 : 0;
	}

}
