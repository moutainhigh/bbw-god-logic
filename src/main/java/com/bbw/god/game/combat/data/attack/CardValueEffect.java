package com.bbw.god.game.combat.data.attack;

import com.bbw.god.game.combat.data.TimesLimit;
import lombok.*;

import java.io.Serializable;

/**
 * 影响卡牌的属性
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 17:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CardValueEffect extends Effect implements Serializable {
	private static final long serialVersionUID = 1L;
	private CardValueEffectType valueType = CardValueEffectType.IN_TIME;
	private int beginRound=0;//初始生效回合
	//---------永久属性值攻击------------
	private int roundAtk = 0;//回合初始物理攻击
	private int roundHp = 0;//回合初始血量
	private int roundMp = 0;//永久的法力
	//---------回合内可恢复的属性值攻击------------
	private int mp = 0;//需要消耗的法力值
	private int atk = 0;//当前物理攻击
	private int hp = 0;//当前血量

	private double atkTimes = 0;// 增减当前物理攻击的倍数 默认倍数为自身*1
	private double hpTmes = 0;// 增减当前血量倍数 默认倍数为自身*1

	private double roundAtkTimes = 0;// 增减当前物理攻击的倍数 默认倍数为自身*1
	private double roundHpTmes = 0;// 增减当前血量倍数 默认倍数为自身*1

	//---------攻击的持续回合数------------
	private TimesLimit timesLimit = TimesLimit.oneTimeLimit();
	public void incAtk(Double val) {
		atk += val.intValue();
	}

	public void incRoundAtk(Double val) {
		roundAtk += val.intValue();
	}

	public void incHp(Double val) {
		hp += val.intValue();
	}

	public void incRoundHp(Double val) {
		roundHp += val.intValue();
	}
	public void setValueType(CardValueEffectType valueType) {
		this.valueType = valueType;
		if (this.valueType == CardValueEffectType.LASTING) {
			timesLimit = TimesLimit.noLimit();
		}
	}

	public void lostTime() {
		timesLimit.lostTimes();
	}

	public boolean isEffective() {
		return timesLimit.hasPerformTimes();
	}

	public boolean isHarmEffect(){
		return (hp+roundHp)<=0 && (mp+roundMp)<=0 &&(atkTimes+hpTmes)<=0;
	}
	/**
	 * 是否是按倍数变化的
	 * 
	 * @return
	 */
	public boolean isMultiple() {
		return atkTimes != 0 || roundAtkTimes != 0 || hpTmes != 0 || roundHpTmes != 0;
	}
	@Override
	public EffectResultType getResultType() {
		return EffectResultType.CARD_VALUE_CHANGE;
	}

	private CardValueEffect(EffectSourceType atkType, int atkId, int targetPos) {
		this.setSourceType(atkType);
		this.setSourceID(atkId);
		this.setTargetPos(targetPos);
	}

	/**
	 * 技能效果
	 * @param skillId
	 * @param targetPos
	 * @return
	 */
	public static CardValueEffect getSkillEffect(int skillId, int targetPos) {
		CardValueEffect effect = new CardValueEffect(EffectSourceType.SKILL, skillId, targetPos);
		return effect;
	}
	/**
	 * 获取持续的技能效果
	 * @param skillId
	 * @param targetPos
	 * @param beginRound 开始的回合
	 * @return
	 */
	public static CardValueEffect getSkillLastingEffect(int skillId, int targetPos,int beginRound) {
		CardValueEffect effect = new CardValueEffect(EffectSourceType.SKILL, skillId, targetPos);
		effect.setBeginRound(beginRound);
		return effect;
	}
	/**
	 * 物理攻击效果
	 *
	 * @param normalSkillId：物理攻击ID
	 * @param targetPos:攻击目标
	 * @return
	 */
	public static CardValueEffect getNormalAttackEffect(int normalSkillId, int targetPos) {
		CardValueEffect effect = new CardValueEffect(EffectSourceType.NORMAL, normalSkillId, targetPos);
		return effect;
	}

	/**
	 * 法宝攻击效果
	 * @param weaponId
	 * @param targetPos
	 * @return
	 */
	public static CardValueEffect getWeaponEffect(int weaponId, int targetPos) {
		CardValueEffect weaponEffect = new CardValueEffect(EffectSourceType.WEAPON, weaponId, targetPos);
		weaponEffect.setAttackPower(AttackPower.getMaxPower());
		return weaponEffect;
	}

	/**
	 * 属性变更类型
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2019-07-24 15:56
	 */
	@Getter
	@AllArgsConstructor
	public static enum CardValueEffectType implements Serializable {
		IN_TIME(1, "马上生效"), DELAY(2, "延迟生效增益减益的，可被防御取消的"), LASTING(4, "持久性的");
		private int type;//属性变更类型
		private String name;
	}
}
