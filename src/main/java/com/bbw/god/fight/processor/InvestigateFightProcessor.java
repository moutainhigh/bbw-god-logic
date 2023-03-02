package com.bbw.god.fight.processor;

import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.yeg.YeGNormalFightProcessor;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 侦查战斗结算
 * @date 2020/6/1 15:15
 **/
@Service
public class InvestigateFightProcessor extends AbstractCityFightProcessor {
    @Autowired
    private YeGNormalFightProcessor yeGNormalFightProcessor;

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_INVESTIGATE;
    }

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.INVESTIGATE;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        GameUser gu = gameUserService.getGameUser(uid);
        checkAbleFight(gu, cache);
        //采用与城池相同属性的 野怪
        CfgCityEntity city = CityTool.getCityById(cache.getCityId());
        RDFightsInfo info = yeGNormalFightProcessor.getFightsInfo(gu, city.getProperty());
        return toCombatPVEParam(info);
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        rd.setOwnCity(0);
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        //结算经验
        int gainExp = getExp(gu, param.getOppLostBlood(), param);
        if (gu.getLevel() >= 30) {
            gainExp = gainExp * 11 / 20;
        } else {
            gainExp = gainExp * 14 / 20;
        }
        int baseCopper = gainExp;
        baseCopper *= (1 + getBaseCopperBuf(gu));
        gainExp *= (1 + getBaseExpBuf(gu, param.getAdditionExp()));
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        // 铜钱加成
        double copperAddRate = getCopperAddRate(param.getZcTimes(), godService.getCopperAddRate(gu));
        int extraCopper = (int) (baseCopper * copperAddRate);
        // 处理战斗铜钱
        EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
        copperAdd.addCopper(ResWayType.Extra, extraCopper);
        ResEventPublisher.pubCopperAddEvent(copperAdd);
        // 落宝金钱发放道具
        userTreasureEffectService.effectAsLBJQ(gu, rd);
        // 发放元素
        int random = PowerRandom.getRandomBySeed(100);
        int addedEleNum = 0;
        if (random > 95) {
            addedEleNum = 3;
        } else if (random > 60) {
            addedEleNum = 1;
        }
        ResEventPublisher.pubEleAddEvent(gu.getId(), addedEleNum, getWay(), rd);

        String opponentName = param.getOpponentName();
        if (StrUtil.isBlank(opponentName)) {
            rd.setWinDes("您在侦查中取得了胜利！");
        } else {
            rd.setWinDes("在侦查中战胜 " + opponentName + " ！");
        }
        rd.setOwnCity(1);

        //标记已打过
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        cache.setInvestigated(true);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), cache);
    }


}
