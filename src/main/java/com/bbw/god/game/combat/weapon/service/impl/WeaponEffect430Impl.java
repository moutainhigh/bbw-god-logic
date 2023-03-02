package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 扫霞衣 	装备卡牌防御永久+500.一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect430Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 430;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //扫霞衣 	装备卡牌防御永久+500.一场战斗限用1次。
        int roundAtk = 0;
        int roundHp = 500;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);

        return ar;
    }
    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setHp(500);
        return rd;
    }

}
