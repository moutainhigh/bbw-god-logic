package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponInTimeEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 紫金钵盂	使用后该回合为我方所有卡牌添加飞行技能。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect320Impl implements IWeaponInTimeEffect {
    @Override
    public int getWeaponId() {
        return 320;
    }

    @Override
    public Action takeInTimeAttack(PerformWeaponParam pwp) {
        //紫金钵盂	使用后该回合为我方所有卡牌添加飞行技能。一场战斗限用1次。
        Action ar = new Action();
        List<BattleCard> handCards = pwp.getCombat().getHandCards(pwp.getPerformPlayerId());
        if (handCards.isEmpty()) {
            throw new ExceptionForClientTip("combat.player.handcard.is.empty");
        }
        int skillId = CombatSkillEnum.FX.getValue();
        for (BattleCard card : handCards) {
            BattleSkillEffect skillEffect = BattleSkillEffect.getWeaponEffect(getWeaponId(), card.getPos());
            //添加飞行技能1回合
            TimesLimit timesLimit = TimesLimit.oneRoundLimit();
            skillEffect.addSkill(skillId, timesLimit);
            pwp.getMultiplePos().add(card.getPos());
            ar.addEffect(skillEffect);
        }
        return ar;
    }
}
