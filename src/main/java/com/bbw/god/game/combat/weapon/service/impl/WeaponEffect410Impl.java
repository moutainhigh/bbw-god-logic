package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 青云剑	 装备卡牌攻防永久+100，一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect410Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 410;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //青云剑	 装备卡牌攻防永久+100，一场战斗限用1次。
        int roundAtk = 100;
        int roundHp = 100;
        Action ar = this.addRoundAtkHp(pwp, roundAtk, roundHp);
        return ar;
    }
    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setHp(100);
        rd.setAtk(100);
        return rd;
    }
}
