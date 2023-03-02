package com.bbw.god.fight.processor;

import com.bbw.App;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CCLevelEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 振兴战斗结算
 *
 * @author suhq
 * @date 2018年11月15日 上午9:22:23
 */
@Service
public class PromoteFightProcessor extends AbstractCityFightProcessor {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private App app;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.PROMOTE;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_PROMOTE;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        //城池缓存
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        GameUser gu = gameUserService.getGameUser(uid);
        checkAbleFight(gu, cache);
        //置为未结算状态
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        if (cache.getPromoteFightParam() == null) {
            CombatPVEParam pveParam = new CombatPVEParam();
            pveParam.setCityBaseId(cache.getCityId());
            pveParam.setCityLevel(cache.getCityLv());
            pveParam.setCityHierarchy(cache.getHv());
            getNormalOpponentParam(gameUserService.getGameUser(uid), cache, pveParam);
            cache.setPromoteFightParam(pveParam);
            TimeLimitCacheUtil.setChengChiInfoCache(uid, cache);
        }
        return cache.getPromoteFightParam();
    }

    @Override
    public CPlayerInitParam getNormalOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam pveParam) {
        UserCity userCity = userCityService.getUserCity(gu.getId(), cache.getCityId());
        List<CCardParam> cardParams = new ArrayList<>();
        int zhsLv = 130;
        if (app.runAsDev()) {
            //游动版本
            int cardHv = (userCity.getHierarchy() + 1) * 2;
            int cardLv = 20;
            zhsLv = 130;
            if (5 != cache.getCityLv()) {
                int owmCityNum = userCityService.getCityNumAsHierarchy(gu.getId(), cache.getCityLv(), cache.getHv() + 1);
                switch (cache.getCityLv()) {
                    case 1:
                        zhsLv = Math.min(85, 60 + owmCityNum);
                        cardLv = Math.min(35, 10 + owmCityNum);
                        break;
                    case 2:
                        zhsLv = Math.min(105, 65 + owmCityNum * 2);
                        cardLv = Math.min(30, 10 + owmCityNum);
                        break;
                    case 3:
                        zhsLv = Math.min(110, 70 + owmCityNum * 2);
                        cardLv = Math.min(30, 10 + owmCityNum);
                        break;
                    case 4:
                        zhsLv = Math.min(105, 75 + owmCityNum * 2);
                        cardLv = Math.min(25, 10 + owmCityNum);
                        break;
                    case 5:
                    default:
                }
            }
            String soliders = CityTool.getCfgPromoteSoliders(cache.getCityId());
            cardParams.addAll(getCardsByPromoteSoliders(soliders, cardLv, cardHv));
        } else {
            //自营版本
            CfgCityEntity city = userCity.gainCity();
            // 升阶需要下一阶数
            int cityLv = city.getLevel();
            int hierarchy = userCity.getHierarchy() + 1;
            int levelToAdd = userCityService.getCityNumAsHierarchy(gu.getId(), cityLv, hierarchy);
            int cardHv = hierarchy * 2;
            ChengC chengc = CityTool.getChengc(cache.getCityId());
            zhsLv = chengc.getSoliderLevel() + levelToAdd + hierarchy * 10;
            cardParams.addAll(getCardsBySoliderString(chengc.getSoliders(), levelToAdd, cardHv, CfgCard.AI_CARDS_NOT_TO_FSDL_2));
        }
        CCardParam cardParam = cardParams.get(0);
        //第一张卡作为AI召唤师的名字
        CPlayerInitParam param = new CPlayerInitParam();
        param.setHeadImg(cardParam.getId());
        param.setNickname(CardTool.getCardById(cardParam.getId()).getName());
        param.setLv(zhsLv);
        param.setCards(cardParams);
        pveParam.setAiPlayer(param);
        return param;
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        CfgCityEntity cityEntity = CityTool.getCityById(cache.getCityId());
        //结算经验
        int gainExp = getExp(gu, param.getOppLostBlood(), param) * 8 / 10;
        int baseCopper = gainExp;
        baseCopper *= (1 + getBaseCopperBuf(gu));
        gainExp *= (1 + getBaseExpBuf(gu, param.getAdditionExp()));
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        // 结算铜钱
        double copperAddRate = getCopperAddRate(param.getZcTimes(), godService.getCopperAddRate(gu));
        int extraCopper = (int) (baseCopper * copperAddRate);
        EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
        copperAdd.addCopper(ResWayType.Extra, extraCopper);
        ResEventPublisher.pubCopperAddEvent(copperAdd);
        // 落宝金钱发放道具
        userTreasureEffectService.effectAsLBJQ(gu, rd);

        // 奖励 掉落卡牌
        CfgCardEntity card = userCardService.getAttackCardAwardForCity(gu.getId(), cache.getCityId());
        CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), getWay(), "振兴了【" + cityEntity.getName() + "】,并获得", rd);

        // 城池阶数 +1
        UserCity userCity = userCityService.getUserCity(gu.getId(), cityEntity.getId());
        userCity.addHierarchy();
        gameUserService.updateItem(userCity);
        // 发布事件
        rd.setOwnCity(1);
        rd.setWinDes("您将" + CCLevelEnum.fromValue(cityEntity.getLevel()).getName() + " " + cityEntity.getName() + "振兴了一级！");
        RDArriveChengC rdArriveChengC = TimeLimitCacheUtil.getChengCCache(gu.getId());
        rdArriveChengC.setToPromote(1);
        TimeLimitCacheUtil.setArriveCache(gu.getId(), rdArriveChengC);
        TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
        //标记已打过
        cache.setPromote(true);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), cache);
    }


    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        // 未攻下
        rd.setOwnCity(1);
        TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
    }

    public List<CCardParam> getCardsByPromoteSoliders(String soliderString, int lv, int hv) {
        List<CCardParam> list = new ArrayList<>();
        String[] cardList = soliderString.split(";");
        for (String cardStr : cardList) {
            String[] split = cardStr.split(",");
            UserCard.UserCardStrengthenInfo info = new UserCard.UserCardStrengthenInfo();
            info.updateCurrentSkill(CardSkillPosEnum.SKILL_0, Integer.parseInt(split[1]));
            info.updateCurrentSkill(CardSkillPosEnum.SKILL_5, Integer.parseInt(split[2]));
            info.updateCurrentSkill(CardSkillPosEnum.SKILL_10, Integer.parseInt(split[3]));
            list.add(CCardParam.init(Integer.parseInt(split[0]), lv, hv, info));
        }
        return list;
    }
}
