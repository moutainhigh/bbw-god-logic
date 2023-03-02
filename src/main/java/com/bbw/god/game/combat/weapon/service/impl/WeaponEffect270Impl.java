package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 莫邪宝剑	装备卡牌攻防永久+380。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect270Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 270;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //莫邪宝剑	装备卡牌攻防永久+380。一场战斗限用1次。
        int roundAtk = 380;
        int roundHp = 380;
        Action ar = addRoundAtkHp(pwp, roundAtk, roundHp);
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setAtk(380);
        rd.setHp(380);
        return rd;
    }
}
