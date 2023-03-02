package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

/**
 *
 * 百毒豆	对敌方一张卡牌使用，使其中毒，每回合永久破除100点防御。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect490Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 490;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //百毒豆	对敌方一张卡牌使用，使其中毒，每回合永久破除100点防御。一场战斗限用1次。(入痘)
        pwp.effectOppoPlayer();
        Action ar = new Action();
        //添加永久伤害
        int skillID=1007;
        CardValueEffect atk = CardValueEffect.getWeaponEffect(skillID, pwp.getBattleCard().getPos());
        atk.setBeginRound(pwp.getCombat().getRound());
        atk.setValueType(CardValueEffect.CardValueEffectType.LASTING);
        int roundHp = -100;
        atk.setRoundHp(roundHp);
        ar.addEffect(atk);
        return ar;
    }

}
