package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 *
 * 攒心钉 每回合针对敌方防御最低的卡牌，破除其250点永久防御。一场战斗限用1次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Slf4j
@Service
public class WeaponEffect300Impl implements IWeaponAfterEffect {
    @Override
    public int getWeaponId() {
        return 300;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        // 攒心钉 每回合针对敌方防御最低的卡牌，破除其250点永久防御。一场战斗限用1次。
        Action ar = new Action();
        List<BattleCard> cards = pwp.getOppoPlayingCards(true);
        if (cards.isEmpty()) {
            return ar;
        }
        // 防御最低
        Optional<BattleCard> targetCard = cards.stream().min(Comparator.comparing(BattleCard::getHp));
        int roundHp = 250;
        if (targetCard.isPresent()) {
            CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), targetCard.get().getPos());
            effect.setRoundHp(-roundHp);
            effect.setSequence(pwp.getNextAnimationSeq());
            pwp.resetPos(targetCard.get().getPos());
            ar.addEffect(effect);
            try {
                FightAchievementCache cache = TimeLimitCacheUtil
                        .getOrCreateFightAchievementCache(pwp.getPerformPlayer().getUid(), pwp.getCombat().getId());
                if (cache != null) {
                    cache.setEffectZanXD(cache.getEffectZanXD() + 1);
                    TimeLimitCacheUtil.setFightAchievementCache(pwp.getPerformPlayer().getUid(), cache);
                }
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    log.error(e.getMessage());
                }
            }
        }
        return ar;

    }

    @Override
    public RDTempResult beforehandAttack(PerformWeaponParam pwp) {
        pwp.setCanEffectTimes(30);
        return new RDTempResult();
    }

}
