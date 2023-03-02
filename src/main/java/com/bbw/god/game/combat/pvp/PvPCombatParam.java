package com.bbw.god.game.combat.pvp;

import com.bbw.god.gameuser.card.UserCard;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lwb
 * @date 2019年8月25日
 * @version 1.0
 */
@Data
public class PvPCombatParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long uid;// 玩家ID
	private Long realUid;// 哪个玩家的卡牌
	private String nickname;
	private Integer lv;
	private List<PvpCard> cards;// 卡牌
	private Integer headImg;
	private Integer initHP;
	private Integer initMP;
	private List<Integer> useTreasures=null;

	private Long cardGroupId;

	@Data
	public static class PvpCard implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer baseId;// 卡牌ID
		private Integer hv;// 阶级
		private Integer lv;// 等级
		private Integer skill0;
		private Integer skill5;
		private Integer skill10;
		private Integer attackSymbol = 0;// 攻击符箓
		private Integer defenceSymbol = 0;// 防御符箓
		private Integer isUseSkillScroll=0;//是否使用了卷轴 0 没有 1 有

		public static PvpCard instance(UserCard userCard){
			PvpCard pvpCard=new PvpCard();
			pvpCard.setBaseId(userCard.getBaseId());
			pvpCard.setHv(userCard.getHierarchy());
			pvpCard.setLv(userCard.getLevel());
			pvpCard.setSkill0(userCard.gainSkill0());
			pvpCard.setSkill5(userCard.gainSkill5());
			pvpCard.setSkill10(userCard.gainSkill10());
			pvpCard.setAttackSymbol(userCard.gainAttackSymbol());
			pvpCard.setDefenceSymbol(userCard.gainDefenceSymbol());
			pvpCard.setIsUseSkillScroll(userCard.ifUseSkillScroll()?1:0);
			return pvpCard;
		}
	}

	@Data
	public static class UptoCard implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long uid;
		private String moveToBattle = "";// 卡牌ID
		private boolean auto = false;// 自动上卡 0 为主动 1 为自动

		public boolean isAuto() {
			return auto;
		}
	}
}
