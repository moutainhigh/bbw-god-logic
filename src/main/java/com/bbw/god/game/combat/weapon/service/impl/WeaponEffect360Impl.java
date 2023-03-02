package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 震天箭 减少敌方召唤师500点血量。一场战斗限用3次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect360Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 360;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();
        //震天箭	减少敌方召唤师500点血量。一场战斗限用3次。
        int oppoZhsPos = PositionService.getZhaoHuanShiPos(pwp.getOppoPlayer().getId());
        CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), oppoZhsPos);
        int hp = 500;
        effect.setHp(-hp);
        ar.addEffect(effect);
        pwp.resetPos(oppoZhsPos);
        return ar;
    }

    @Override
    public int getPerformTotalTimes() {
        return 3;
    }

    @Override
    public int getPerformRoundTimes() {
        return 3;
    }

}
