package com.bbw.god.fight.processor;

import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.yed.RDArriveYeD;
import com.bbw.god.city.yeg.YeGEliteFightProcessor;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 野地事件战斗结算
 * @date 2020/6/1 15:15
 **/
@Service
public class YeDFightProcessor extends AbstractFightProcessor {
    @Autowired
    protected UserCityService userCityService;
    @Autowired
    private MonthLoginLogic monthLoginLogic;
    @Autowired
    private YeGEliteFightProcessor yeGEliteFightProcessor;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.YED_EVENT;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_YED_EVENT;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        RDArriveYeD cache = TimeLimitCacheUtil.getArriveCache(uid, RDArriveYeD.class);
        RDFightsInfo fightsInfo = cache.getFightsInfo();
        return toCombatPVEParam(fightsInfo);
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {

    }


    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        RDArriveYeD rdArriveYeD = TimeLimitCacheUtil.getArriveCache(gu.getId(), RDArriveYeD.class);
        RDFightsInfo fightsInfo = rdArriveYeD.getFightsInfo();
        Integer yeDEventType = fightsInfo.getYeDEventType();
        YdEventEnum ydEventEnum = YdEventEnum.fromValue(yeDEventType);
        if (null == ydEventEnum) {
            return;
        }
        long uid = gu.getId();
        RDArriveYeD arriveYeD = TimeLimitCacheUtil.getArriveCache(gu.getId(), RDArriveYeD.class);
        switch (ydEventEnum) {
            case DA_BING:
            case QIANG_DAO:
                Long copper = arriveYeD.getDeductCopper();
                ResEventPublisher.pubCopperAddEvent(uid, copper, WayEnum.YD, rd);
                break;
            case XIAO_TOU:
                List<Integer> specialIds = arriveYeD.getReduceSpcialIds();
                List<EVSpecialAdd> list = specialIds.stream().map(EVSpecialAdd::new).collect(Collectors.toList());
                SpecialEventPublisher.pubSpecialAddEvent(uid, list, WayEnum.YD, rd);
                break;
            case XIN_MO:
                TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.TongTCJ.getValue(), 1, WayEnum.YD, rd);
                break;
            default:
                break;
        }
    }

    public RDFightsInfo buildRDFightsInfo(long uid, int cityLv, int cityId, YdEventEnum type) {
        GameUser gu = gameUserService.getGameUser(uid);
        boolean toElite = false;
        switch (type) {
            case DA_BING:
                toElite = monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.BAD_DAJI);
                break;
            case QIANG_DAO:
                toElite = monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.BAD_QD);
                break;
            case XIAO_TOU:
                toElite = monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.BAD_XT);
                break;
            default:
        }
        if (toElite) {
            CfgCityEntity city = CityTool.getCityById(cityId);
            return yeGEliteFightProcessor.getFightsInfo(gu, city.getProperty());
        }
        int hv = 0;
        UserCity userCity = userCityService.getUserCity(gu.getId(), cityId);
        if (userCity != null) {
            hv = userCity.getHierarchy();
        }
        //练兵卡组
        int levelToAdd = this.userCityService.getCityNumAsHierarchy(gu.getId(), cityLv, hv);
        if (cityLv <= 3) {
            int lv = gu.getLevel();
            for (int i = 30; i > 0; i -= 10) {
                if (lv < i) {
                    levelToAdd -= 1;
                } else {
                    continue;
                }
            }
        } else if (cityLv == 5) {
            levelToAdd *= 3;
        }
        levelToAdd = Math.max(levelToAdd, 0);
        int cardHv = hv * 2;
        ChengC chengc = CityTool.getChengc(cityId);
        int zhsLv = chengc.getSoliderLevel() + levelToAdd + hv * 10;
        List<CCardParam> cardParams = null;
        if (gu.getStatus().ifNotInFsdlWorld()) {
            cardParams = getCardsBySoliderString(chengc.getSoliders(), levelToAdd, cardHv, null);
        } else {
            cardParams = getCardsBySoliderString(chengc.getSoliders(), levelToAdd, cardHv, CfgCard.AI_CARDS_NOT_TO_FSDL_2);
        }
        RDFightsInfo info = new RDFightsInfo();
        List<RDFightsInfo.RDFightCard> cards = new ArrayList<>();
        for (CCardParam card : cardParams) {
            cards.add(RDFightsInfo.RDFightCard.instance(card));
        }
        info.setLevel(zhsLv);
        info.setCards(cards);
        return info;
    }
}
