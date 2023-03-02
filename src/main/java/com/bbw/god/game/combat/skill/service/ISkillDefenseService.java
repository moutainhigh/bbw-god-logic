package com.bbw.god.game.combat.skill.service;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public interface ISkillDefenseService extends ISkillBaseService {

    default Effect.AttackPower getDefensePower() {
        return Effect.AttackPower.L1;
    }

    default Action takeDefense(PerformSkillParam psp) {
        Action attackResult = psp.getDefenseAction();
        // 如果不是收到技能伤害，则不发动技能
        if (!psp.receiveSkillEffect()) {
            return attackResult;
        }
        // 如果防御能力比收到的伤害效果低，则不可防御
        if (getDefensePower().getValue() < psp.getReceiveEffect().getAttackPower().getValue()) {
            attackResult.setTakeEffect(false);
            return attackResult;
        }
        // 不在防守技能列表中
        int receiveEffectSkillId = psp.getReceiveEffectSkillId();
        if (!inDefenseTable(receiveEffectSkillId)) {
            attackResult.setTakeEffect(false);
            return attackResult;
        }
        // 清除作用自身的技能效果
        attackResult.getEffects().clear();
        attackResult.setTakeEffect(true);
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
                    Logger log = LoggerFactory.getLogger(this.getClass());
                    log.error(e.getMessage());
                }
            }
        }
        return attackResult;
    }
    default boolean inDefenseTable(int receiveEffectSkillId){
        int[] defenseSkillIds = BattleSkillDefenseTableService.getDefenseTableBySkillId(this.getMySkillId());
        return this.contains(defenseSkillIds, receiveEffectSkillId);
    }
}
