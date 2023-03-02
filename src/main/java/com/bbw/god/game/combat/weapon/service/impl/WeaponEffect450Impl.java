package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 柴胡草 治疗一张卡牌受到的所有永久性伤害。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect450Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 450;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 柴胡草 治疗一张卡牌受到的所有永久性伤害。一场战斗限用1次。
        BattleCard targetCard = pwp.getBattleCard();
        int initAtk = targetCard.getInitAtk();
        int initHp = targetCard.getInitHp();
        if (PositionService.isXianFengPos(targetCard.getPos())) {
            initAtk = getInt(1.1 * initAtk);
        }
        if (PositionService.isZhongJunPos(targetCard.getPos())) {
            initHp = getInt(1.15 * initHp);
        }
        int cAtk = initAtk - targetCard.getRoundAtk();
        int cHp = initHp - targetCard.getRoundHp();
        cAtk = cAtk > 0 ? cAtk : 0;
        cHp = cHp > 0 ? cHp : 0;
        Action ar = this.addRoundAtkHp(pwp, cAtk, cHp);
        return ar;
    }

}
