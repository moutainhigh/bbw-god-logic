package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 五火神焰扇	破除敌方全体卡牌150~500点防御（无视金刚）。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect440Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 440;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        //五火神焰扇	破除敌方全体卡牌150~500点防御（无视金刚）。一场战斗限用1次。
        Action ar = new Action();
        List<BattleCard> playingCards =pwp.getOppoPlayingCards(true);
        if (playingCards.isEmpty()) {
            return ar;
        }
        int atk = PowerRandom.getRandomBetween(150, 500);
        int sequence=pwp.getNextAnimationSeq();
        for (BattleCard targetCard : playingCards) {
            CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), targetCard.getPos());
            effect.setSequence(sequence);
            effect.setHp(-atk);
            ar.addEffect(effect);
            pwp.getMultiplePos().add(targetCard.getPos());
        }
        return ar;
    }
}
