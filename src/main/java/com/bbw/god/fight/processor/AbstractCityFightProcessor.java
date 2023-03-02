package com.bbw.god.fight.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.treasure.UserTreasureEffectService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author：lwb
 * @date: 2020/12/21 10:37
 * @version: 1.0
 */
public abstract class AbstractCityFightProcessor extends AbstractFightProcessor {
    @Autowired
    protected UserTreasureEffectService userTreasureEffectService;
    @Autowired
    protected UserAttackDifficultyLogic attackDifficultyLogic;
    @Autowired
    protected UserCityService userCityService;

    @Override
    public void settleBefore(GameUser gu, FightSubmitParam param) {
        Optional<UserGod> userGod = godService.getAttachGod(gu);
        if (userGod.isPresent()) {
            UserGod god = userGod.get();
            int attachGod = god.getBaseId();
            if ((attachGod == GodEnum.TJ.getValue() || attachGod == GodEnum.XJ.getValue()
                    || attachGod == GodEnum.TB.getValue() || attachGod == GodEnum.XB.getValue())) {
                god.deductRemainStep(1);
                if (god.getRemainStep() <= 0) {
                    godService.setUnvalid(gu, god);
                } else {
                    gameUserService.updateItem(god);
                }
            }
        }
    }

    public void checkAbleFight(GameUser gu, ChengChiInfoCache cache) {
        if (cache == null) {
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        FightTypeEnum type = getFightType();
        switch (type) {
            case ATTACK:
                if (cache.isAttack()) {
                    throw new ExceptionForClientTip("fight.cant.repeat");
                }
                break;
            case TRAINING:
                if (cache.isTraining()) {
                    throw new ExceptionForClientTip("fight.cant.repeat");
                }
                break;
            case PROMOTE:
                if (cache.isPromote()) {
                    throw new ExceptionForClientTip("fight.cant.repeat");
                }
                break;
        }
        return;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        //城池缓存
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        GameUser gu = gameUserService.getGameUser(uid);
        checkAbleFight(gu, cache);
        //置为未结算状态
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        if (fightAgain) {
            //再战则从缓存中获取对手
            return cache.getFightParam();
        }
        CombatPVEParam param = new CombatPVEParam();
        param.setCityBaseId(cache.getCityId());
        param.setCityLevel(cache.getCityLv());
        param.setCityHierarchy(cache.getHv());
        if (gu.getStatus().intoNightmareWord()) {
            getNightmareOpponentParam(gu, cache, param);
        } else {
            getNormalOpponentParam(gu, cache, param);
        }
        //对手信息缓存
        cache.setFightParam(param);
        TimeLimitCacheUtil.setChengChiInfoCache(uid, cache);
        return param;
    }


    /**
     * 获取封神大陆对手
     *
     * @return
     */
    public CPlayerInitParam getNormalOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam param) {
        throw new ExceptionForClientTip("nightmare.not.this.type.fightinfo");
    }

    /**
     * 获取梦魇对手
     *
     * @return
     */
    public CPlayerInitParam getNightmareOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam param) {
        return getNormalOpponentParam(gu, cache, param);
    }
}
