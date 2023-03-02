package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponInTimeEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 混元金斗	使用后，该回合所有手牌的召唤所需法力值减少1，每回合只能使用1次，一场战斗限用3次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect400Impl implements IWeaponInTimeEffect {
    @Override
    public int getWeaponId() {
        return 400;
    }


    @Override
    public int getPerformTotalTimes() {
        return 3;
    }

    @Override
    public int getPerformRoundTimes() {
        return 1;
    }

    @Override
    public Action takeInTimeAttack(PerformWeaponParam pwp) {
        Action ar = new Action();
        //混元金斗	使用后，该回合所有手牌的召唤所需法力值减少1，每回合只能使用1次，一场战斗限用3次。
        List<BattleCard> handCards = pwp.getCombat().getHandCards(pwp.getPerformPlayerId());
        if (handCards.isEmpty()) {
            throw new ExceptionForClientTip("combat.player.handcard.is.empty");
        }
        for (BattleCard card : handCards) {
            if (card.mpMoreThanMin()) {
                CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), card.getPos());
                effect.setMp(-1);
                ar.addEffect(effect);
            }
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setMp(-1);
        return rd;
    }
}
