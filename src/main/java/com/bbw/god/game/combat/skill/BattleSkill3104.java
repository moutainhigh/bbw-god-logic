package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 3104 封神 每回合随机将1张我方坟场的卡牌拉回战场，如战场已满则进入牌组。
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 00:09
 */
@Service
public class BattleSkill3104 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.FS.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {

		Action ar = buildAction(psp);
		if (!ar.existsEffect()) {
			return ar;
		}
		boolean hasMoveToBattle = ar.getEffects().stream().anyMatch(tmp -> {
			if (tmp.isPositionEffect()) {
				return false;
			}
			CardPositionEffect positionEffect = (CardPositionEffect) tmp;
			return positionEffect.getToPositionType() == PositionType.BATTLE;
		});
		if (hasMoveToBattle) {
			fenShenToBattle(psp.getPerformPlayer().getUid(), psp.getCombat().getId());
		}
		return ar;
	}

	public Action buildAction(PerformSkillParam psp) {
		Action ar = new Action();
		// 3104 封神 每回合随机将1张我方坟场的卡牌拉回战场，如战场已满则进入牌组。
		Player me = psp.getPerformPlayer();
		List<BattleCard> discards = me.getDiscard();
		if (discards.isEmpty()) {
			return ar;
		}

		BattleCard card = PowerRandom.getRandomFromList(discards);
		CardPositionEffect effect = CardPositionEffect.getSkillEffectToTargetPos(SKILL_ID, card.getPos());
		effect.setAttackPower(AttackPower.getMaxPower());
		effect.setSequence(psp.getNextAnimationSeq());
		// 随机到云台卡，并且云台位空
		if (card.canFly() && me.yunTaiIsEmpty()) {
			// 设置到云台
			int yunTaiPos = PositionService.getYunTaiPos(me.getId());
			effect.moveTo(PositionType.BATTLE, yunTaiPos);
			ar.addEffect(effect);
			return ar;
		}

		// 寻找战场空位
		int[] emptyBattlePos = me.getEmptyBattlePos(false);
		if (emptyBattlePos.length > 0) {
			// 到战场
			int index = PowerRandom.randomInt(emptyBattlePos.length);
			effect.moveTo(PositionType.BATTLE, emptyBattlePos[index]);
		} else {
			// 到牌堆
			effect.moveTo(PositionType.DRAWCARD);
		}
		ar.addEffect(effect);
		return ar;
	}

	private void fenShenToBattle(long uid, long combatId) {
		try {
			FightAchievementCache cache = TimeLimitCacheUtil.getOrCreateFightAchievementCache(uid, combatId);
			if (cache != null) {
				cache.setEffectFenS(cache.getEffectFenS() + 1);
				TimeLimitCacheUtil.setFightAchievementCache(uid, cache);
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				log.error(e.getMessage());
			}
		}
	}
}
