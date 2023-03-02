package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 红葫芦	回复我方召唤师600点血量。一场战斗限用5次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect370Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 370;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();

        //红葫芦	回复我方召唤师600点血量。一场战斗限用5次。
        int oppoZhsPos = PositionService.getZhaoHuanShiPos(pwp.getPerformPlayerId());
        CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), oppoZhsPos);
        int hp = 600;
        effect.setHp(hp);
        ar.addEffect(effect);
        pwp.resetPos(oppoZhsPos);
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setHp(600);
        return rd;
    }

    @Override
    public int getPerformTotalTimes() {
        return 5;
    }

    @Override
    public int getPerformRoundTimes() {
        return 5;
    }
}
