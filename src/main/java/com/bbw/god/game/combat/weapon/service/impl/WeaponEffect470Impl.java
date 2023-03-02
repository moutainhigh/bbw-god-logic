package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 *  太极符印 装备卡牌获得无相技能。一张战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect470Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 470;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 太极符印 装备卡牌获得无相技能。一张战斗限用1次。
        pwp.effectSelf();
        Action ar = new Action();

        BattleCard effectCard = pwp.getBattleCard();
        // TODO: 4001 无相
        int skillId = 4001;
        Optional<BattleSkillEffect> effectOptional = addBattleSkillEffect(effectCard, skillId, TimesLimit.noLimit());
        if (effectOptional.isPresent()) {
            ar.addEffect(effectOptional.get());
        }
        return ar;
    }

}
