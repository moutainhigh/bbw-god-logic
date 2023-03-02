package com.bbw.god.game.combat.weapon.service.impl;

import com.bbw.App;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.combat.FightAchievementCache;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 乾坤弓 减少敌方召唤师450-800血量。一场战斗限用3次。
 * @author：lwb
 * @date: 2020/11/25 9:36
 * @version: 1.0
 */
@Slf4j
@Service
public class WeaponEffect350Impl implements IWeaponAfterEffect {
    @Autowired
    private App app;

    @Override
    public int getWeaponId() {
        return 350;
    }

    @Override
    public Action takeAfterAttack(PerformWeaponParam pwp) {
        Action ar = new Action();

        // 乾坤弓 减少敌方召唤师450-800血量。一场战斗限用3次。
        int oppoZhsPos = PositionService.getZhaoHuanShiPos(pwp.getOppoPlayer().getId());
        int hp = PowerRandom.getRandomBetween(450, 800);
        if (app.runAsDev()){
            hp=9000000;
        }
        CardValueEffect effect = CardValueEffect.getWeaponEffect(getWeaponId(), oppoZhsPos);
        effect.setHp(-hp);
        effect.setSequence(pwp.getNextAnimationSeq());
        ar.addEffect(effect);
        pwp.resetPos(oppoZhsPos);
        try {
            FightAchievementCache cache = TimeLimitCacheUtil
                    .getOrCreateFightAchievementCache(pwp.getPerformPlayer().getUid(), pwp.getCombat().getId());
            if (cache != null) {
                cache.addLoseHpByQianKG(hp);
                TimeLimitCacheUtil.setFightAchievementCache(pwp.getPerformPlayer().getUid(), cache);
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                log.error(e.getMessage());
            }
        }
        return ar;
    }

    @Override
    public int getPerformTotalTimes() {
        return 3;
    }

    @Override
    public int getPerformRoundTimes() {
        return 3;
    }

}
