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
 * 戮仙剑 装备卡牌攻击永久+500且获得技能嗜血。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect290Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 290;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 戮仙剑 装备卡牌攻击+500且获得特技嗜血。
        int roundAtk = 500;
        int roundHp = 0;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);
        BattleCard effectCard = pwp.getBattleCard();
        // 嗜血4203
        int skillId = 4203;
        Optional<BattleSkillEffect> effectOptional = addBattleSkillEffect(effectCard, skillId, TimesLimit.noLimit());
        if (effectOptional.isPresent()) {
            ar.addEffect(effectOptional.get());
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd = new RDTempResult();
        rd.setAtk(500);
        return rd;
    }

}
