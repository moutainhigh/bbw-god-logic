package com.bbw.god.game.combat.skill.magicdefense;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通用防守技能
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-08 15:32
 */
@Service
@Slf4j
public abstract class BattleSkillDefenseService implements ISkillDefenseService {
	@Override
	public boolean match(int skillId) {
		return getMySkillId() == skillId;
	}

	@Override
	public Effect.AttackPower getDefensePower() {
		return Effect.AttackPower.L1;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action attackResult = psp.getDefenseAction();
		// 如果不是收到技能伤害，则不发动技能
		if (!psp.receiveSkillEffect()) {
			attackResult.setTakeEffect(false);
			return attackResult;
		}
		// 如果防御能力比收到的伤害效果低，则不可防御
		if (getDefensePower().getValue() < psp.getReceiveEffect().getAttackPower().getValue()) {
			attackResult.setTakeEffect(false);
			return attackResult;
		}
		// 不在防守技能列表中
		int[] defenseSkillIds = BattleSkillDefenseTableService.getDefenseTableBySkillId(this.getMySkillId());
		int receiveEffectSkillId = psp.getReceiveEffectSkillId();
		if (!this.contains(defenseSkillIds, psp.getReceiveEffectSkillId())) {
			attackResult.setTakeEffect(false);
			return attackResult;
		}
		// 清除作用自身的技能效果
		attackResult.getEffects().clear();
		attackResult.setTakeEffect(true);
		psp.getReceiveEffect().setDefended(true);
		psp.setReceiveEffect(null);
		if (this.getMySkillId() == 8002) {
			try {
				FightAchievementCache cache = TimeLimitCacheUtil
						.getOrCreateFightAchievementCache(psp.getPerformPlayer().getUid(), psp.getCombat().getId());
				if (cache != null) {
					cache.getDingSDEffect().add(receiveEffectSkillId);
					TimeLimitCacheUtil.setFightAchievementCache(psp.getPerformPlayer().getUid(), cache);
				}
			} catch (Exception e) {
				if (e.getMessage() != null) {
					log.error(e.getMessage());
				}
			}
		}
		return attackResult;
	}


}
