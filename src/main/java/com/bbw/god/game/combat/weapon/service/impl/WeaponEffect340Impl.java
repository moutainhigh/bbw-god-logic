package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
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
 * 绝仙剑 装备卡牌攻防各+250且获得特技穿刺。 一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect340Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 340;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
       // 绝仙剑 装备卡牌攻防永久+250且获得技能穿刺。一场战斗限用1次。
        int roundAtk = 250;
        int roundHp = 250;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);
        BattleCard effectCard = pwp.getBattleCard();

        // TODO: 4201 穿刺
        int skillId = 4201;
        Optional<BattleSkillEffect> effectOptional = addBattleSkillEffect(effectCard, skillId, TimesLimit.noLimit());
        if (effectOptional.isPresent()) {
            ar.addEffect(effectOptional.get());
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd = new RDTempResult();
        rd.setAtk(250);
        rd.setHp(250);
        return rd;
    }
}
