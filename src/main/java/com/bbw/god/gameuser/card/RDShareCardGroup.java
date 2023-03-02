package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.wanxianzhen.WanXianCard;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquipment;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月8日 下午5:50:49 
* 类说明 
*/
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class RDShareCardGroup extends RDSuccess  implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name=null;// 卡组名称
	private Long uid=null;//来自哪个玩家
	private List<RDCard> cards=null;
	
	private String shareId=null;
	
	public void addCards(List<UserCard> userCards) {
		if (cards==null){
			cards=new ArrayList<>();
		}
		userCards.stream().forEach(p->cards.add(RDCard.instance(p)));
	}
	public void addCard(UserLeaderCard leaderCard,UserLeaderEquipment[] equips,int[] beasts,String cardName) {
		if (cards==null){
			cards=new ArrayList<>();
		}
		RDCard instance = RDCard.getInstance(leaderCard, equips, beasts);
		instance.setCardName(cardName);
		cards.add(instance);
	}
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@NoArgsConstructor
	public static class RDCard implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer baseId = null;
		private Integer level = null;
		private Integer soul = null;
		private Integer hierarchy = null;
		private Long experience = null;
		private Integer skill0;
		private Integer skill5;
		private Integer skill10;
		private Integer attackSymbol = 0;// 攻击符箓
		private Integer defenceSymbol = 0;// 防御符箓
		private Integer isUseSkillScroll;// 是否使用技能卷轴
		private Integer sex;
		private Integer property;
		private Integer star;
		private Integer fashion;
		/**
		 * 装备
		 */
		private UserLeaderEquipment[] equips = null;
		/**
		 * 宠物
		 */
		private int[] beasts = null;
		private Integer hp;
		private Integer atk;
		private String cardName;
		public static RDCard instance(UserCard uc) {
			RDCard rd = new RDCard();
			rd.setBaseId(uc.getBaseId());
			rd.setLevel(uc.getLevel());
			rd.setSoul(uc.getLingshi());
			rd.setHierarchy(uc.getHierarchy());
			long exp = uc.getExperience() - CardExpTool.getExpByLevel(uc.gainCard(), uc.getLevel());
			rd.setExperience(exp);
			rd.setSkill0(uc.gainSkill0());
			rd.setSkill5(uc.gainSkill5());
			rd.setSkill10(uc.gainSkill10());
			rd.setDefenceSymbol(uc.gainDefenceSymbol());
			rd.setAttackSymbol(uc.gainAttackSymbol());
			rd.setIsUseSkillScroll(uc.ifUseSkillScroll() ? 1 : 0);
			return rd;
		}

		public static RDCard instance(WanXianCard wanXianCard){
			RDCard rd = new RDCard();
			rd.setBaseId(wanXianCard.getCardId());
			rd.setAttackSymbol(wanXianCard.getAttackSymbol());
			rd.setDefenceSymbol(wanXianCard.getDefenceSymbol());
			rd.setHierarchy(wanXianCard.getHv());
			rd.setLevel(wanXianCard.getLv());
			rd.setIsUseSkillScroll(wanXianCard.getIsUseSkillScroll());
			rd.setSkill0(wanXianCard.getSkill0());
			rd.setSkill5(wanXianCard.getSkill5());
			rd.setSkill10(wanXianCard.getSkill10());
			return rd;
		}

		public static RDCard getInstance(UserLeaderCard leaderCard,UserLeaderEquipment[] equips,int[] beasts){
			RDCard rd = new RDCard();
			rd.setBaseId(CardEnum.LEADER_CARD.getCardId());
			rd.setAttackSymbol(0);
			rd.setDefenceSymbol(0);
			rd.setHierarchy(leaderCard.getHv());
			rd.setLevel(leaderCard.getLv());
			rd.setIsUseSkillScroll(0);
			int[] skills = leaderCard.currentSkills();
			rd.setSkill0(skills[0]);
			rd.setSkill5(skills[1]);
			rd.setSkill10(skills[2]);
			rd.setSex(leaderCard.getSex());
			rd.setProperty(leaderCard.getProperty());
			rd.setStar(leaderCard.getStar());
			rd.setFashion(leaderCard.getFashion());
			rd.setEquips(equips);
			rd.setBeasts(beasts);
			rd.setHp(leaderCard.settleTotalHpWithEquip());
			rd.setAtk(leaderCard.settleTotalAtkWithEquip());
			return rd;
		}
	}
}
