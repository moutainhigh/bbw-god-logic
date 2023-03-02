package com.bbw.god.game.combat.data.attack;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 影响卡牌的位置
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 17:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CardPositionEffect extends Effect implements Serializable {
	private static final long serialVersionUID = 1L;
	private PositionType toPositionType = PositionType.HAND;//被攻击者的需要移动到哪个牌堆
	private int toPos = -1;
	private transient CardMovement movement = null;//位置移动
	private boolean exchange=false;

	/**
	 * 指定了移动位置
	 * @return
	 */
	public boolean hasToPos() {
		return toPos > 0;
	}

	public int getFromPos() {
		return this.getTargetPos();
	}

	/**
	 * 设置移动卡牌参数
	 * @param toPositionType 牌堆
	 * @param toPos 牌堆里的位置
	 */
	public void moveTo(PositionType toPositionType, int... toPos) {

		this.toPositionType = toPositionType;
		if (toPos.length > 0) {
			this.toPos = toPos[0];
		}
	}

	public PositionType getFromPositionType() {
		return PositionService.getPositionType(getFromPos());
	}

	public CardMovement getCardMovement() {
		if (movement == null) {
			movement = new CardMovement(getFromPos(), toPos);
		}
		return new CardMovement(getFromPos(), toPos);
	}

	@Override
	public EffectResultType getResultType() {
		return EffectResultType.CARD_POSITION_CHANGE;
	}

	private CardPositionEffect(EffectSourceType sourceType) {
		this.setSourceType(sourceType);
	}
	public CardPositionEffect(EffectSourceType sourceType, int atkId, int fromPos,int targetPos) {
		this.setSourceType(sourceType);
		this.setSourceID(atkId);
		this.setTargetPos(targetPos);
		this.setSourcePos(fromPos);
	}

	/**
	 * 技能效果
	 * @param skillId
	 * @param targetPos
	 * @return
	 */
	public static CardPositionEffect getSkillEffectToTargetPos(int skillId,int targetPos) {
		CardPositionEffect effect = new CardPositionEffect(EffectSourceType.SKILL);
		effect.setSourceID(skillId);
		effect.setTargetPos(targetPos);
		return effect;
	}
	/**
	 * 法宝攻击效果
	 * @return
	 */
	public static CardPositionEffect getWeaponEffect(int weaponId, int fromPos, PositionType toPositionType) {
		CardPositionEffect weaponEffect = getSkillEffectToTargetPos(weaponId,fromPos);
		weaponEffect.setSourceType(EffectSourceType.WEAPON);
		weaponEffect.moveTo(toPositionType);
		weaponEffect.setAttackPower(AttackPower.getMaxPower());
		return weaponEffect;
	}
	
	public static CardPositionEffect getWeaponEffectMoveToPos(int weaponId, int fromPos, int toPos) {
		CardPositionEffect weaponEffect = getSkillEffectToTargetPos(weaponId,fromPos);
		weaponEffect.setSourceType(EffectSourceType.WEAPON);
		weaponEffect.setToPos(toPos);
		weaponEffect.setAttackPower(AttackPower.getMaxPower());
		return weaponEffect;
	}
}
