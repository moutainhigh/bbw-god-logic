package com.bbw.god.game.combat.data.attack;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus.StatusEffectType;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 影响卡牌的技能
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 17:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BattleSkillEffect extends Effect implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer lastRound = 1;// 持续的回合数
	private BattleSkillEffectType skillEffectType = BattleSkillEffectType.LIMIT;
	private StatusEffectType lastingEffectType = null;// 技能延迟效果
	private BattleCard souceCard = null;
	private List<BattleSkillEffectLimit> effectLimits = new ArrayList<>();

	@Override
	public EffectResultType getResultType() {
		return EffectResultType.SKILL_STATUS_CHANGE;
	}

	/**
	 * 给卡牌添加技能
	 * 
	 * @param skillId
	 *            技能ID
	 * @param rounds
	 *            添加的技能可使用的回合数
	 */
	public void addSkill(int skillId, TimesLimit timesLimit) {
		this.skillEffectType = BattleSkillEffectType.ADD;
		BattleSkillEffectLimit limit = new BattleSkillEffectLimit(skillId, timesLimit);
		effectLimits.add(limit);
	}

	/**
	 * 法术限制、状态buff
	 * 
	 * @param skillId
	 * @param targetPos
	 * @return
	 */
	public void changeSkillAttackTarget(int skillId, int attackPos) {
		this.skillEffectType = BattleSkillEffectType.CHANGE_TARGET;
		BattleSkillEffectLimit limit = new BattleSkillEffectLimit(skillId, TimesLimit.oneTimeLimit());
		limit.setAttackPos(attackPos);
		effectLimits.add(limit);
	}

	/**
	 * 封锁一回合
	 * 
	 * @param skillId
	 * @param fromId 发出封禁的技能
	 */
	public void forbidOneRound(int skillId, TimesLimit timesLimit,int fromId) {
		BattleSkillEffectLimit limit = new BattleSkillEffectLimit(skillId, timesLimit);
		limit.getTimesLimit().forbidOneRound(fromId);
		effectLimits.add(limit);
	}

	/**
	 * 永久封锁
	 * @param skill  被封锁的技能ID
	 * @param fromId 发出封禁的技能
	 */
	public void forbid(BattleSkill skill,int fromId) {
		TimesLimit timesLimit = CloneUtil.clone(skill.getTimesLimit());
		BattleSkillEffectLimit limit = new BattleSkillEffectLimit(skill.getId(), timesLimit);
		limit.getTimesLimit().forbid();
		limit.getTimesLimit().setBanFrom(fromId);
		effectLimits.add(limit);
	}

	private BattleSkillEffect(EffectSourceType atkType, int atkId, int targetPos) {
		this.setSourceType(atkType);
		this.setSourceID(atkId);
		this.setTargetPos(targetPos);
	}

	/**
	 * 技能效果
	 * 
	 * @param skillId
	 * @param targetPos
	 * @return
	 */
	public static BattleSkillEffect getSkillEffect(int skillId, int targetPos) {
		BattleSkillEffect effect = new BattleSkillEffect(EffectSourceType.SKILL, skillId, targetPos);
		return effect;
	}

	/**
	 * 法宝攻击效果
	 * 
	 * @param atkId
	 * @param targetPos
	 * @return
	 */
	public static BattleSkillEffect getWeaponEffect(int weaponId, int targetPos) {
		BattleSkillEffect weaponEffect = new BattleSkillEffect(EffectSourceType.WEAPON, weaponId, targetPos);
		weaponEffect.setAttackPower(AttackPower.getMaxPower());
		return weaponEffect;
	}

	public static BattleSkillEffect getLastPerformSkillEffect(StatusEffectType type, int skillId, int targetPos) {
		BattleSkillEffect effect = new BattleSkillEffect(EffectSourceType.SKILL, skillId, targetPos);
		effect.setSkillEffectType(BattleSkillEffectType.LASTING);
		effect.setLastingEffectType(type);
		return effect;
	}
	@Data
	public static class BattleSkillEffectLimit implements Serializable {
		private static final long serialVersionUID = 1L;
		// @Getter
		private int skillId = -1;// 技能ID
		private int attackPos = -1;// 当前技能的攻击目标
		// @Getter
		private TimesLimit timesLimit = TimesLimit.noLimit();// 剩余可使用次数

		public BattleSkillEffectLimit(int skillId, TimesLimit timesLimit) {
			this.skillId = skillId;
			this.timesLimit = timesLimit;
		}

		public boolean isTargetAttack() {
			return attackPos > 0;
		}
	}

	/**
	 * 技能攻击类型
	 * 
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-07-24 15:56
	 */
	@Getter
	@AllArgsConstructor
	public static enum BattleSkillEffectType implements Serializable {
		ADD(1, "添加技能"), LIMIT(2, "限制技能"), LASTING(3, "延迟触发技能具体实现逻辑"), CHANGE_TARGET(4, "改变技能攻击目标"),ADD_STATUS(5, "添加技能状态");
		private int type;// 属性变更类型
		private String name;
	}
}
