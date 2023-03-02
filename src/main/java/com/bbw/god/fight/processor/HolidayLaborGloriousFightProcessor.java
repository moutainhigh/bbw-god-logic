package com.bbw.god.fight.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.holiday.config.CfgHolidayLaborGlorious;
import com.bbw.god.activity.holiday.config.HolidayLarGloriousTool;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.yeg.IYegFightProcessor;
import com.bbw.god.city.yeg.YeGFightProcessorFactory;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动劳动光荣战斗类
 *
 * @author fzj
 * @date 2022/4/13 15:46
 */
@Service
public class HolidayLaborGloriousFightProcessor extends AbstractFightProcessor {
    @Autowired
    YeGFightProcessorFactory yeGFightProcessorFactory;
    @Autowired
    private AwardService awardService;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.LABOR_FIGHT;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_YG;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);

        GameUser gu = gameUserService.getGameUser(uid);
        YeGuaiEnum yeGuaiEnum = YeGuaiEnum.YG_ELITE;
        IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(yeGuaiEnum);
        int yeGuaiType = yeGuaiEnum.getType();
        //获得战斗信息
        RDFightsInfo fightsInfo = fightProcessor.getFightsInfo(gu, yeGuaiType);
        List<CCardParam> cardParams = new ArrayList<>();
        //添加战斗卡组
        for (RDFightsInfo.RDFightCard card : fightsInfo.getCards()) {
            cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
        }
        CfgHolidayLaborGlorious cfgHolidayLaborGlorious = HolidayLarGloriousTool.getCfg();
        String nickNme = cfgHolidayLaborGlorious.getNpcName();
        CPlayerInitParam ai = CPlayerInitParam.initParam(fightsInfo.getLevel(), nickNme, cfgHolidayLaborGlorious.getHeadImg(), fightsInfo.getHeadIcon());
        ai.setCards(cardParams);
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setYgType(yeGuaiType);
        pveParam.setAiPlayer(ai);
        return pveParam;
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {

    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        CfgHolidayLaborGlorious cfgHolidayLaborGlorious = HolidayLarGloriousTool.getCfg();

        //随机奖励次数
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < cfgHolidayLaborGlorious.getYeGuaiFightOutputNum(); i++) {
            Award award = getAward();
            awards.add(award);
        }
        awardService.fetchAward(gu.getId(), awards, WayEnum.LABOR_BOX, "", rd);
    }

    /**
     * 根据概率获得奖励
     *
     * @return
     */
    private Award getAward() {
        CfgHolidayLaborGlorious cfgHolidayLaborGlorious = HolidayLarGloriousTool.getCfg();
        List<Award> awards = cfgHolidayLaborGlorious.getYeGuaiFightOutput();
        List<Integer> proList = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(proList, cfgHolidayLaborGlorious.getEventTotalPro());
        return awards.get(index);
    }
}
