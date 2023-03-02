package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *3145 落羽 每回合将对方云台位卡牌射落至随机阵位，阵位已满则回到卡组。（优先手牌，满了回牌组）
 */
@Service
public class BattleSkill3145 extends BattleSkillService {
	private static final int SKILL_ID = 3145;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//每回合将对方云台位卡牌射落至随机阵位，阵位已满则回到卡组。（优先手牌，满了回牌组）
		Optional<BattleCard> yunTaiPosCardOp = psp.getOppoPlayer().findYunTaiPosCard();
		if (!yunTaiPosCardOp.isPresent()){
			return ar;
		}
		BattleCard targetCard=yunTaiPosCardOp.get();
		int[] emptyBattlePos=psp.getOppoPlayer().getEmptyBattlePos(false);
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, targetCard.getPos());
		effect.setSequence(psp.getNextAnimationSeq());
		if (emptyBattlePos.length>0){
			//存在空位
			int index = PowerRandom.randomInt(emptyBattlePos.length);
			effect.moveTo(PositionType.BATTLE, emptyBattlePos[index]);
		}else {
			//否则优先去手牌
			effect.moveTo(PositionType.DRAWCARD);
		}
		ar.addEffect(effect);
		return ar;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp) {
		Action ar = new Action();
		//每回合将对方云台位卡牌射落至随机阵位，阵位已满则回到卡组。（优先手牌，满了回牌组）
		int[] emptyBattlePos=psp.getOppoPlayer().getEmptyBattlePos(false);
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, target.getPos());
		effect.setSequence(psp.getNextAnimationSeq());
		if (emptyBattlePos.length>0){
			//存在空位
			int index = PowerRandom.randomInt(emptyBattlePos.length);
			effect.moveTo(PositionType.BATTLE, emptyBattlePos[index]);
		}else {
			//否则优先去手牌
			effect.moveTo(PositionType.DRAWCARD);
		}
		ar.addEffect(effect);
		return ar;
	}
}
