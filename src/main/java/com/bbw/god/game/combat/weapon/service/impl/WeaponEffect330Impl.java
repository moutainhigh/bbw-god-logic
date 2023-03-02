package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *  紫绫仙衣	我方场上所有卡牌防御永久+100.一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Service
public class WeaponEffect330Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 330;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar=new Action();
        // 紫绫仙衣	我方场上所有卡牌防御永久+100.一场战斗限用1次。
        List<BattleCard> playingCards =pwp.getPerformPlayerPlayingCards(true);
        if (playingCards.isEmpty()) {
            return ar;
        }
        int roundHp = 100;
        int seq=pwp.getNextAnimationSeq();
        for(BattleCard card:playingCards) {
            CardValueEffect effect=CardValueEffect.getSkillEffect(getWeaponId(), card.getPos());
            effect.setSequence(seq);
            effect.setRoundHp(roundHp);
            ar.addEffect(effect);
            pwp.getMultiplePos().add(card.getPos());
        }
        return ar;
    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        RDTempResult rd=new RDTempResult();
        rd.setHp(100);
        return rd;
    }
}
