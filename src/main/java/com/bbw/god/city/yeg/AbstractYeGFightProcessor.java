package com.bbw.god.city.yeg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.AbstractSpecialYeGuaiProcessor;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidaySpecialYeGuaiFactory;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidayYeGuaiProcessor;
import com.bbw.god.activityrank.server.winbox.WinBoxRankEventPublisher;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.yeg.event.EPOpenYeGuaiBox;
import com.bbw.god.city.yeg.event.YeGuaiEventPublisher;
import com.bbw.god.detail.AwardDetail;
import com.bbw.god.detail.DetailData;
import com.bbw.god.detail.disruptor.DetailEventHandler;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.OpponentCardsUtil;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.combat.exaward.YeGExawardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YgTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月26日 下午11:43:47
 * 类说明
 */
@Service
public abstract class AbstractYeGFightProcessor implements IYegFightProcessor {

    @Autowired
    private UserCityService userCityService;
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected PrivilegeService privilegeService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    protected AwardService awardService;
    @Autowired
    protected BoxService boxService;
    @Autowired
    private HolidayYeGuaiProcessor holidayYeGuaiProcessor;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private HolidaySpecialYeGuaiFactory holidaySpecialYeGuaiFactory;
    @Autowired
    private YeGBoxService yeGBoxService;

    public abstract WayEnum getWay();

    /**
     * 获取符文Id
     *
     * @return
     */
    public abstract int getRunesId();

    // 开哪种宝箱 默认野怪相同类型的宝箱
    public YeGuaiEnum yeGuaiBoxType() {
        return getYeGEnum();
    }

    ;

    /**
     * 生成野怪卡牌
     *
     * @param gu
     * @param type
     * @return
     */
    public RDFightsInfo generateRDFightsInfo(GameUser gu, int type) {
        // 常规野怪卡牌
        int ygLevel = 0;
        //int cardLevelToAdd = 0;
        if (gu.getLevel() < 11) {
            ygLevel = Math.min(gu.getLevel(),
                    gu.getLevel() + 1 - PowerRandom.getRandomBySeed(Math.min(gu.getLevel(), 3)));
        } else if (gu.getLevel() < 21) {
            int random = PowerRandom.getRandomBySeed(11);
            ygLevel = random + gu.getLevel() - 5;
            //String[] ygWins = gu.getStatus().getYgWin().split(",");
            //	cardLevelToAdd = Integer.valueOf(ygWins[random - 1]) / 3;
        } else {
            int random = PowerRandom.getRandomBySeed(11);
            ygLevel = random + gu.getLevel();
            //	String[] ygWins = gu.getStatus().getYgWin().split(",");
            //	cardLevelToAdd = Integer.valueOf(ygWins[random - 1]) / 3;
        }
        int maxYgLevel = YgTool.getMaxYGLevel();
        ygLevel = ygLevel > maxYgLevel ? maxYgLevel : ygLevel;

        List<Integer> excludeCardIds = new ArrayList<>();
        excludeCardIds.add(CardEnum.JIN_LING_SHENG_MU.getCardId());
        excludeCardIds.add(CardEnum.CHI_JING_ZI.getCardId());
        excludeCardIds.add(CardEnum.TAI_YXJ.getCardId());
        if (!gu.getStatus().ifNotInFsdlWorld()) {
            excludeCardIds.add(CardEnum.HONG_YAO.getCardId());
            excludeCardIds.add(CardEnum.WEN_DAO_REN.getCardId());
            excludeCardIds.add(CardEnum.GAO_JI_NENG.getCardId());
            excludeCardIds.add(CardEnum.QIU_YU.getCardId());
        }

        CfgYeGuaiEntity ygCards = YgTool.getYGCards(ygLevel);
        List<UserCard> opponentCards = OpponentCardsUtil.getOpponentCardsForMonster(ygCards.getCards(), type, excludeCardIds);
        return new RDFightsInfo(ygLevel, opponentCards);
    }

    /**
     * @param gu
     * @param type 五行属性
     * @return
     */
    @Override
    public RDFightsInfo getFightsInfo(GameUser gu, int type) {
        RDFightsInfo info = generateRDFightsInfo(gu, type);
        int hv = addhv(gu.getId(), type);
        if (hv > 0) {
            for (RDFightsInfo.RDFightCard card : info.getCards()) {
                card.setHierarchy(hv);
            }
        }
        CfgCardEntity cardEntity = CardTool.getCardById(info.getCards().get(0).getBaseId());
        info.setNickname(cardEntity.getName());
        info.setHead(cardEntity.getId());
        if (gu.getLevel() >= 24) {
            //普通怪召唤师等级=玩家等级±5级(上限120级）
            int max = gu.getLevel() + 5;
            int min = gu.getLevel() - 5;
            int targetLevel = PowerRandom.getRandomBetween(min, max);
            if (targetLevel > 120) {
                targetLevel = 120;
            }
            info.setLevel(targetLevel);
        }
        info.setHeadIcon(TreasureEnum.HEAD_ICON_Normal.getValue());
        if (gu.getStatus().ifNotInFsdlWorld()) {
            info.setCityBuff(getRunesId());
        }
        return info;
    }

    @Override
    public RDCommon openBox(RDFightEndInfo fightEndInfo, long guId) {
        int remainTime = fightEndInfo.getRemainTime();
        if (remainTime == 0) {
            throw new ExceptionForClientTip("yg.openbox.not.remain");
        }
        GameUser gu = gameUserService.getGameUser(guId);
        int freeTime = fightEndInfo.getFreeTime();
        int needGold = YgTool.getYgConfig().getYgBoxPrice();
        if (freeTime > 0) {
            needGold = 0;
            freeTime--;
        }
        remainTime--;
        RDCommon rd = new RDCommon();
        if (needGold > 0) {
            ResChecker.checkGold(gu, needGold);
            ResEventPublisher.pubGoldDeductEvent(guId, needGold, getWay(), rd);
            ActionStatisticTool.addUserActionStatistic(guId, 1, getWay().getName());
        }
        sendBoxAward(fightEndInfo, gu, rd);
        doActivityEvent(guId, fightEndInfo, rd);
        // 胜利宝箱榜
        WinBoxRankEventPublisher.pubWinBoxEvent(guId);
        // 记录明细
        AwardDetail awardDetail = AwardDetail.fromTreasure(TreasureEnum.FIGHT_BX, 1);
        DetailData detailData = DetailData.instance(gu, getWay(), awardDetail);
        detailData.setAfterValue((long) remainTime);
        DetailEventHandler.getInstance().log(detailData);

        fightEndInfo.setFreeTime(freeTime);
        fightEndInfo.setRemainTime(remainTime);
        TimeLimitCacheUtil.setFightEndCache(guId, fightEndInfo);
        TimeLimitCacheUtil.getArriveCache(guId, RDArriveYeG.class);
        rd.setFreeTimes(freeTime);
        return rd;
    }

    /**
     * 执行活动事件
     *
     * @param guId
     * @param fightEndInfo
     * @param rd
     */
    private void doActivityEvent(long guId, RDFightEndInfo fightEndInfo, RDCommon rd) {
        //活动特殊野怪额外奖励
        AbstractSpecialYeGuaiProcessor specialYeGuaiProcessor = holidaySpecialYeGuaiFactory.getSpecialYeGuaiProcessor(guId);
        if (null == specialYeGuaiProcessor) {
            return;
        }
        specialYeGuaiProcessor.sendActivityExtraAward(guId, fightEndInfo, rd);
    }

    @Override
    public void sendBoxAward(RDFightEndInfo fightEndInfo, GameUser gu, RDCommon rd) {
        long guId = gu.getId();
        double copperAddRate = fightEndInfo.getCopperAddRate();
        YeGuaiEnum yeGtype = fightEndInfo.getYeGtype();
        copperAddRate = Math.max(copperAddRate, -0.2);
        boolean businessGang = fightEndInfo.isBusinessGang();
        CfgYeGuai.YeGBoxConfig yeGBoxConfig = YgTool.getYgConfig().gainBoxConfig(yeGtype, gu.getLevel(), businessGang);
        yeGBoxService.openYeGuaiBox(guId, yeGtype, yeGBoxConfig.getBoxKey(), fightEndInfo.getOpenBoxTypes(), copperAddRate, getWay(), businessGang, rd);
        BaseEventParam bep = new BaseEventParam(guId, WayEnum.YG_ELITE_OPEN_BOX, rd);
        YeGuaiEventPublisher.pubOpenYeGuaiBoxEvent(new EPOpenYeGuaiBox(yeGtype, bep));
    }

    @Override
    public List<Award> getRandomBoxAwards(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        CfgYeGuai.YeGBoxConfig yeGBoxConfig = YgTool.getYgConfig().gainBoxConfig(getYeGEnum(), gu.getLevel(), false);
        if (null == yeGBoxConfig) {
            return new ArrayList<>();
        }
        List<Award> awards = yeGBoxService.getAward(uid, yeGBoxConfig.getBoxKey());
        if (null == awards) {
            return new ArrayList<>();
        }
        return awards;
    }

    /**
     * 获得额外任务目标
     *
     * @return
     */
    @Override
    public YeGExawardEnum getAdditionGoal() {
        return YeGExawardEnum.randomNormal();
    }

    /**
     * 野怪卡牌阶级加成
     *
     * @param uid
     * @param type
     * @return
     */
    protected Integer addhv(long uid, Integer type) {
        List<UserCity> cities = userCityService.getOwnCitiesByCountry(uid, type);
        if (ListUtil.isEmpty(cities)) {
            return 0;
        }
        int add = 0;
//		①　西岐城区的所有城池全部被玩家攻占后，金系野怪格中的野怪的阶数提升为1阶。
        int countryCities = CityTool.getCCCities(type).size();
        if (countryCities == cities.size()) {
            add = 1;
        } else {
            return add;
        }
//		②　西岐城区的所有城池全部首次振兴后，金系野怪格中的野怪的阶数提升为2阶。
        long hvNum = cities.stream().filter(p -> p.getHierarchy() > 0).count();
        if (countryCities == hvNum) {
            add = 2;
        } else {
            return add;
        }
//		③　西岐城区中任意9座城池达到振兴二后，金系野怪格中的野怪的阶数提升为3阶。
        long hv2Num = cities.stream().filter(p -> p.getHierarchy() >= 2).count();
        if (hv2Num >= 9) {
            add = 3;
        } else {
            return add;
        }
//		④　西岐城区中所有城池全部达到振兴二后，金系野怪格中的野怪的阶数提升为4阶。
        if (countryCities == hv2Num) {
            add = 4;
        } else {
            return add;
        }
//		⑤　西岐城区中任意9座城池达到振兴三后，金系野怪格中的野怪的阶数提升为5阶。
        long hv3Num = cities.stream().filter(p -> p.getHierarchy() >= 3).count();
        if (hv3Num >= 9) {
            add = 5;
        } else {
            return add;
        }
//		⑥　西岐城区中所有城池全部达到振兴三后，金系野怪格中的野怪的阶数提升为6阶。
        if (countryCities == hv3Num) {
            add = 6;
        } else {
            return add;
        }
//		⑦　西岐城区中任意9座城池达到振兴四后，金系野怪格中的野怪的阶数提升为7阶。
        long hv4Num = cities.stream().filter(p -> p.getHierarchy() >= 4).count();
        if (hv4Num >= 9) {
            add = 7;
        } else {
            return add;
        }
//		⑧　西岐城区中所有城池全部达到振兴四后，金系野怪格中的野怪的阶数提升为8阶。
        if (countryCities == hv4Num) {
            add = 8;
        } else {
            return add;
        }
//		⑨　西岐城区中任意9座城池达到振兴五后，金系野怪格中的野怪的阶数提升为9阶。
        long hv5Num = cities.stream().filter(p -> p.getHierarchy() >= 5).count();
        if (hv5Num >= 9) {
            add = 9;
        } else {
            return add;
        }
//		⑩　西岐城区中所有城池全部达到振兴五后，金系野怪格中的野怪的阶数提升为10阶。
        if (countryCities == hv5Num) {
            add = 10;
        }
        return add;
    }
}