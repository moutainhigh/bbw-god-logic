package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 九龙神火罩 永久封锁敌方一张卡牌，使其不能攻击，不能使用技能直到其死亡。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect260Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 260;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();
        //九龙神火罩 永久封锁敌方一张卡牌，使其不能攻击，不能使用技能直到其死亡。一场战斗限用1次。【枷锁】
        pwp.effectOppoPlayer();
        int sequence=pwp.getNextAnimationSeq();
        BattleCard targetCard=pwp.getBattleCard();
        int skillID=3112;//枷锁（与枷锁）
        BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(skillID, targetCard.getPos());
        effect.setLastRound(Integer.MAX_VALUE);
        for (BattleSkill skill : targetCard.getActiveAttackSkills()) {
            effect.forbid(skill,getWeaponId());
            effect.setSequence(sequence);
            effect.setAttackPower(Effect.AttackPower.getMaxPower());
        }
        ar.addEffect(effect);
        return ar;
    }

}
