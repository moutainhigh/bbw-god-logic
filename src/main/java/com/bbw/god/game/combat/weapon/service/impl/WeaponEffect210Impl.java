package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 番天印 将敌方场上1张卡牌直接送入墓地，无视金刚。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect210Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 210;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //不能对己方使用
        pwp.effectOppoPlayer();
        Action ar = new Action();
        //番天印，将敌方场上1张卡牌直接送入墓地，无视金刚。
        CardPositionEffect effect = CardPositionEffect.getWeaponEffect(getWeaponId(), pwp.getTargetPos(), PositionType.DISCARD);
        ar.addEffect(effect);
        return ar;
    }

}
