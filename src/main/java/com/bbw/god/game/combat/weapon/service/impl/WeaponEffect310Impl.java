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
 * 乾坤尺 装备卡牌攻击永久+400且获得技能钻地。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect310Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 310;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 乾坤尺 装备卡牌攻击永久+400且获得技能钻地。一场战斗限用1次。
        int roundAtk = 400;
        int roundHp = 0;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);
        BattleCard effectCard = pwp.getBattleCard();
        // 4302 钻地
        int skillId = 4302;
        Optional<BattleSkillEffect> effectOptional = addBattleSkillEffect(effectCard, skillId, TimesLimit.noLimit());
        if (effectOptional.isPresent()) {
            ar.addEffect(effectOptional.get());
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd = new RDTempResult();
        rd.setAtk(400);
        return rd;
    }

}
