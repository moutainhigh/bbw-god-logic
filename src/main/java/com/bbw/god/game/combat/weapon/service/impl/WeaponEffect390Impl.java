package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 陷仙剑 装备卡牌攻防各+200。 一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect390Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 390;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //陷仙剑 装备卡牌攻防各+200。
        int roundAtk = 200;
        int roundHp = 200;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);

        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setHp(200);
        rd.setAtk(200);
        return rd;
    }

}
