package com.bbw.god.game.combat.data.card;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 
 * 卡牌状态
 * 
 * @author lwb
 * @date 2019年8月19日
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleCardStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer round = 1;// 持续的回合数
	private Integer roundTimes=Integer.MAX_VALUE;//回合可释放次数
	private Integer performTimes=Integer.MAX_VALUE;//当前回合可释放次数
	private Integer skillID;// 状态ID 即 技能或者法宝ID
	private StatusEffectType statusEffectType = StatusEffectType.LASTING;
	private BattleCard souceCard = null;// 状态来源卡
	public BattleCardStatus(int round, int skillId) {
		this.round = round;
		this.skillID = skillId;
	}

	public BattleCardStatus(int round, int skillId, StatusEffectType type) {
		this.round = round;
		this.skillID = skillId;
		this.statusEffectType = type;
	}

	public void incRound() {
		round--;
		performTimes=roundTimes;
	}
	public void incTimes() {
		performTimes--;
	}
	/**
	 * 是否已经失效
	 * 
	 * @return
	 */
	public boolean isInvalid() {
		return round <= 0 ||(round==1&&performTimes==0);
	}
	public boolean isRoundvalid() {
		return performTimes > 0;
	}

	public boolean isNormalAttackType() {
		return statusEffectType == StatusEffectType.NORMAL_ATTACK;
	}

	public static boolean isNeedAddAnimation(StatusEffectType type) {
		StatusEffectType[] need = {StatusEffectType.ROUND_END, StatusEffectType.LASTING,StatusEffectType.ONE_ROUND_LASTING};
		for (StatusEffectType t : need) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 状态作用时机
	 * 
	 * @author lwb
	 * @date 2019年8月23日
	 * @version 1.0
	 */
	@Getter
	@AllArgsConstructor
	public static enum StatusEffectType implements Serializable {
		NORMAL_ATTACK(1, "物理攻击时生效"),
		ROUND_END(2, "回合结束时生效"),
		LASTING(3, "持久性的"),
		ONE_ROUND_LASTING(4,"本回合内都有效");
		private int type;// 属性变更类型
		private String name;
	}
}
