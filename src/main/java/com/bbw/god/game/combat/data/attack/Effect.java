package com.bbw.god.game.combat.data.attack;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.skill.SkillSection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 行动效果
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 17:18
 */
@Data
@NoArgsConstructor
public abstract class Effect implements Serializable {
	private static final long serialVersionUID = 7987228383700369034L;
	private Integer fromCardId = -1;
	private EffectSourceType sourceType = EffectSourceType.NORMAL;// 伤害类型0|1|2|3。0物理伤害，1单体法术伤害，2组合技能伤害，3法宝
	private int sourcePos = -1;// 攻击者的位置信息
	private int sourceID = 0;// 根据atkType类型，依次为卡牌ID，法术ID，组合ID，法宝ID
	private int performSkillID = 0;// Effect为卡牌触发时释放技能的id，sourceID为客户端接收到的技能ID效果
	private int targetPos = -1;// 被攻击者的位置信息
	private int sequence = -1;//
	private AttackPower attackPower = AttackPower.L1;
	private List<Integer> extraSkillEffect = null;// 额外的技能效果 如被地劫技能改变的攻击对象，打伤害的是普攻4401，但是地劫效果打死的不能触发死亡技能
	private boolean particleSkill = false;
	private boolean fromParticleEffect = false;
	private boolean needAnimation = true;//是否需要物理攻击动画NormalSkillRoundService.normalDefense中调用
	/** 是否被防御 用于向倾国被金刚防御后需要移除一些动画 */
	private boolean isDefended;
	/** 该效果是否还有效。由灵敏词条引入 */
	private boolean valid = true;

	public abstract EffectResultType getResultType();

	/**
	 * 由法术技能产生的行动效果
	 *
	 * @return
	 */
	public boolean isFromSkill() {
		return EffectSourceType.SKILL == this.getSourceType();
	}
	/**
	 * 是否是组合技产生的效果
	 * 
	 * @return
	 */
	public boolean isFromGroupSkill() {
		SkillSection groupSection = SkillSection.getGroupSkillSection();
		return groupSection.contains(sourceID);
	}

	public boolean isAttakHpEffect() {
		if (isValueEffect()) {
			CardValueEffect valueEffect = toValueEffect();
			if (valueEffect.getHp() < 0 || valueEffect.getRoundHp() < 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isAttakMpEffect() {
		if (isValueEffect()) {
			CardValueEffect valueEffect = toValueEffect();
			if (valueEffect.getMp() < 0 || valueEffect.getRoundMp() < 0) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取产生该效果的 所有参与技能Id
	 * @return
	 */
	public  List<Integer> getReceiveAllSkillIds(){
		List<Integer> ids=new ArrayList<>();
		if (performSkillID!=0 && sourceID!=88888 ){
			ids.add(performSkillID);
		}
		if (extraSkillEffect!=null && !extraSkillEffect.isEmpty()){
			ids.addAll(extraSkillEffect);
		}
		return ids.stream().map(Integer::intValue).collect(Collectors.toList());
	}

	/**
	 * 获取所有可以含有溅射效果的技能
	 * @return
	 */
	public  List<Integer> getReceiveAllParticleSkillIds(){
		SkillSection allParticleSkills = SkillSection.getAllParticleSkills();
		Set<Integer> ids=new HashSet<>();
		if (performSkillID!=0 && sourceID!=88888 && allParticleSkills.contains(performSkillID) ){
			ids.add(performSkillID);
		}
		if (extraSkillEffect!=null && !extraSkillEffect.isEmpty()){
			for (int id:extraSkillEffect){
				if (allParticleSkills.contains(id)){
					ids.add(id);
				}
			}
		}
		return ids.stream().collect(Collectors.toList());
	}

	public void addExtraSkillEffect(int skillId) {
		if (extraSkillEffect == null) {
			extraSkillEffect = new ArrayList<Integer>();
		}
		extraSkillEffect.add(skillId);
	}

	public void addExtraSkillEffects(List<Integer> ids) {
		if (extraSkillEffect == null) {
			extraSkillEffect = new ArrayList<Integer>();
		}
		extraSkillEffect.addAll(ids);
	}

	public boolean hasExtraSkillEffect(int skillId) {
		if (extraSkillEffect == null) {
			return false;
		}
		return extraSkillEffect.contains(skillId);
	}
	/**
	 * 是否作用己方
	 * 
	 * @return
	 */
	public boolean isEffectSelf() {
		if (sourcePos < 10) {
			// 组合技未设置来源
			return false;
		}
		PlayerId source = PositionService.getPlayerIdByPos(sourcePos);
		PlayerId target = PositionService.getPlayerIdByPos(targetPos);
		return source == target;
	}
	/**
	 * 最强攻击不可防御
	 * 
	 * @return
	 */
	public boolean isMaxPower() {
		return AttackPower.getMaxPower() == attackPower;
	}

	public CardValueEffect toValueEffect() {
		if (this.getResultType() != EffectResultType.CARD_VALUE_CHANGE) {
			throw CoderException.high("无法将[" + getResultType() + "]类型转为ValueEffect类型！");
		}
		return (CardValueEffect) this;
	}

	/**
	 * 是否是卡牌位移攻击
	 * 
	 * @return
	 */
	public boolean isPositionEffect() {
		return this.getResultType() == EffectResultType.CARD_POSITION_CHANGE;
	}

	public boolean isValueEffect() {
		return this.getResultType() == EffectResultType.CARD_VALUE_CHANGE;
	}

	public CardPositionEffect toPositionEffect() {
		if (this.getResultType() != EffectResultType.CARD_POSITION_CHANGE) {
			throw CoderException.high("无法将[" + getResultType() + "]类型转为PositionEffect类型！");
		}
		return (CardPositionEffect) this;
	}

	public BattleSkillEffect toBattleSkillEffect() {
		if (this.getResultType() != EffectResultType.SKILL_STATUS_CHANGE) {
			throw CoderException.high("无法将[" + getResultType() + "]类型转为BattleSkillEffect类型！");
		}
		return (BattleSkillEffect) this;
	}

	public BattleCardChangeEffect toBattleCardChangeEffect() {
		if (this.getResultType() != EffectResultType.CARD_CHANGE_TO_CARD) {
			throw CoderException.high("无法将[" + getResultType() + "]类型转为BattleCardChangeEffect类型！");
		}
		return (BattleCardChangeEffect) this;
	}

	public void replaceEffectSkillId(int skillId){
		this.performSkillID=skillId;
		this.sourceID=skillId;
	}
	public String printString() {
		StringBuilder sb = new StringBuilder();
		sb.append("第(" + this.sequence + ")步行动，");
		sb.append("阵位[" + this.sourcePos + "]");
		sb.append("发动[" + this.sourceType.getName() + "]");
		// switch (this.sourceType) {
		// case NORMAL:
		// CfgCardEntity card = CardTool.getCardById(sourceID);
		// if (null != card) {
		// sb.append("[" + this.sourceID +
		// CardTool.getCardById(sourceID).getName() + "]");
		// } else {
		// sb.append("[" + this.sourceID + "]");
		// }
		// break;
		// default:
		// sb.append("[" + this.sourceID +
		// SkillEnum.fromValue(this.sourceID).getName() + "]");
		// }
		sb.append("[" + this.sourceID + CombatSkillEnum.fromValue(this.sourceID).getName() + "]");
		sb.append("攻击阵位[" + this.targetPos + "]");
		return sb.toString();
	}
	// public CardStatusEffect toBuffStatusEffect() {
	// if (this.getResultType() != EffectResultType.CARD_STATUS_CHANGE) {
	// throw CoderException.high("无法将[" + getResultType() +
	// "]类型转为BuffEffectt类型！");
	// }
	// return (CardStatusEffect) this;
	// }

	/**
	 * 技能、物理攻击、法宝产生的效果类型
	 * 
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-07-09 17:22
	 */
	@Getter
	@AllArgsConstructor
	public static enum EffectResultType implements Serializable {
		// 客户端标识同客户端动画
		PLAY_ANIMATION(10, true, "卡牌触发行动"),
		CARD_VALUE_CHANGE(20, true, "卡牌属性值变更"),
		// CARD_STATUS_CHANGE(30, true, "卡牌状态变更"),
		CARD_POSITION_CHANGE(90, true, "卡牌位置变更"), 
		CARD_CHANGE_TO_CARD(50, true, "卡牌变形	,卡牌完全变为另一张卡牌"),
		SKILL_STATUS_CHANGE(30, false, "卡牌技能变更"),
		CARD_ADD(60, false, "卡牌召唤");
		private int value;// 客户端动画协议值
		private boolean needAnimation;// 是否需要创建动画
		private String name;

		public static EffectResultType fromValue(int value) {
			for (EffectResultType item : values()) {
				if (item.getValue() == value) {
					return item;
				}
			}
			return null;
		}
	}

	/**
	 * 0物理伤害，1单体法术伤害，2组合技能伤害，3法宝
	 * 
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-07-01 11:20
	 */
	@Getter
	@AllArgsConstructor
	public static enum EffectSourceType implements Serializable {
		// 0物理伤害，1单体法术伤害，2组合技能伤害，3法宝
		NORMAL(0, "物理攻击"), SKILL(1, "法术攻击"), WEAPON(3, "法宝攻击"), RECOVER(4, "恢复");
		;
		private int value;
		private String name;
	}

	/**
	 * <pre>
	 * 法术攻击和防御能力。防御能力>攻击能力，则可防御。
	 * 正常攻击为L1,可以被法术防御。
	 * 回光的攻击为L2,超过一般的一般的法术防御，多数无法被防御。
	 * 返照的防御力为L3,可以防御回光的攻击。
	 * 法宝的伤害力为L3,不可防御
	 * </pre>
	 * 
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-07-08 20:30
	 */
	@Getter
	@AllArgsConstructor
	public static enum AttackPower implements Serializable {
		// 攻击效果的破坏能力,用来决定是否可以被防御。
		L1(1), L2(2), L3(3);
		private int value;

		public static AttackPower getMaxPower() {
			return AttackPower.L3;
		}
	}
}
