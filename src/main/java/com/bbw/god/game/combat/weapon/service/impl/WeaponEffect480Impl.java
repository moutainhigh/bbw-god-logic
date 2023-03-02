package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 火龙标	使用后永久破除敌方一张卡牌150点防御。一场战斗限用3次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect480Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 480;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //火龙标	使用后永久破除敌方一张卡牌150点防御。一场战斗限用3次。
        Action ar = new Action();
        pwp.effectOppoPlayer();
        if (pwp.getBattleCard()==null){
            return ar;
        }
        int roundHp = -150;
        CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), pwp.getBattleCard().getPos());
        effect.setRoundHp(roundHp);

        ar.addEffect(effect);

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
