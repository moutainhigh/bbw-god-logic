package com.bbw.god.activity.holiday.processor.holidaycutetugermarket;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.cfg.CfgCuteTigerEntity;
import com.bbw.god.activity.cfg.CfgLittleTigerStoreEntity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.AbstractSpecialCityProcessor;
import com.bbw.god.activity.rd.RDCuteTigerMarketInfo;
import com.bbw.god.activity.rd.RDLittleTigerStoreInfo;
import com.bbw.god.activity.rd.RDRefreshLittleTigerStore;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.city.yed.RDArriveYeD;
import com.bbw.god.game.award.*;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 萌虎集市实现类
 *
 * @author fzj
 * @date 2022/3/4 13:52
 */
@Service
public class HolidayCuteTigerMarketProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    HolidayCuteTigerMarketService holidayCuteTigerMarketService;
    @Autowired
    UserTreasureService userTreasureService;
    @Autowired
    UserCardService userCardService;
    @Autowired
    AwardService awardService;
    /** 活动产出 */
    public static final List<Integer> ACTIVITY_OUTPUT = Arrays.asList(
            TreasureEnum.XIAO_TANG_YUAN.getValue(),
            TreasureEnum.NIAN_GAO.getValue(),
            TreasureEnum.YU_BING.getValue());

    public HolidayCuteTigerMarketProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CUTE_TIGER_MARKET);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDCuteTigerMarketInfo rd = new RDCuteTigerMarketInfo();
        //自动出售糕点
        soldNotTodayPastry(uid, rd);
        //获得已触发事件
        List<RDCuteTigerMarketInfo.SpecialEvents> hasTriggerEvents = new ArrayList<>();
        Map<String, Integer> specialEvents = holidayCuteTigerMarketService.getSpecialEvents();
        for (Map.Entry<String, Integer> specialEvent : specialEvents.entrySet()) {
            String key = specialEvent.getKey();
            long userId = Long.parseLong(key.split(SPLIT)[0]);
            GameUser gameUser = gameUserService.getGameUser(userId);
            String triggerNickname = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + gameUser.getRoleInfo().getNickname();
            RDCuteTigerMarketInfo.SpecialEvents event = new RDCuteTigerMarketInfo.SpecialEvents();
            event.setId(specialEvent.getValue());
            event.setTriggerNickname(triggerNickname);
            hasTriggerEvents.add(event);
        }
        rd.setHasTriggerEvents(hasTriggerEvents);
        //糕点以及售价
        List<RDCuteTigerMarketInfo.PastriesSellPrice> pastriesSellPrices = new ArrayList<>();
        for (Integer pastryId : ACTIVITY_OUTPUT) {
            int sellPrice = getSellPrice(pastryId);
            RDCuteTigerMarketInfo.PastriesSellPrice pastriesSellPrice = new RDCuteTigerMarketInfo.PastriesSellPrice();
            pastriesSellPrice.setPastryId(pastryId);
            pastriesSellPrice.setPrice(sellPrice);
            pastriesSellPrices.add(pastriesSellPrice);
        }
        rd.setPastriesSellPrices(pastriesSellPrices);
        rd.setRemainRefreshTime(DateUtil.getTimeToNextHour() / 1000);
        return rd;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return ACTIVITY_OUTPUT;
    }

    @Override
    public int getTradeBuyPrice(int goodId) {
        TreasureEnum treasureEnum = TreasureEnum.fromValue(goodId);
        switch (treasureEnum) {
            case XIAO_TANG_YUAN:
                return 2000;
            case NIAN_GAO:
                return 4000;
            case YU_BING:
                return 6000;
            default:
                return 0;
        }
    }

    /**
     * 是否最后一天
     *
     * @param uid
     * @return
     */
    protected boolean isLastDay(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CUTE_TIGER_MARKET);
        return DateUtil.isToday(a.gainEnd());
    }

    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return new ArrayList<>();
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            return new ArrayList<>();
        }
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }

    /**
     * 售出不是今日的糕点
     *
     * @param uid
     */
    private void soldNotTodayPastry(long uid, RDCuteTigerMarketInfo rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.actived");
        }
        int totalLittleTigerCoin = 0;
        for (Integer pastryId : ACTIVITY_OUTPUT) {
            int needDelPastryNum = 0;
            Map<Integer, Integer> notTodayPastryNum = holidayCuteTigerMarketService.getNotTodayPastryNum(uid, pastryId);
            if (notTodayPastryNum.isEmpty()) {
                continue;
            }
            for (Map.Entry<Integer, Integer> notTodayPastry : notTodayPastryNum.entrySet()) {
                Integer pastryNum = notTodayPastry.getValue();
                if (pastryNum <= 0) {
                    continue;
                }
                Integer date = notTodayPastry.getKey();
                //获得售价
                int pastryInitialPrice = (int) getLastDaySellPrice(pastryId, date);
                //获得可得到的小虎币
                int littleTigerCoin = pastryNum * pastryInitialPrice;
                totalLittleTigerCoin += littleTigerCoin;
                //需要扣除糕点数量
                needDelPastryNum += pastryNum;
                //清除数据
                holidayCuteTigerMarketService.delDatePastryNum(uid, pastryId, date, pastryNum);
            }
            if (needDelPastryNum > 0) {
                //扣除糕点
                TreasureEventPublisher.pubTDeductEvent(uid, pastryId, needDelPastryNum, WayEnum.CUTE_TIGER_MARKET_AUTOMATIC_DEL, rd);
            }
        }
        if (totalLittleTigerCoin > 0) {
            //增加小虎币
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.LITTLE_TIGER_COIN.getValue(), totalLittleTigerCoin, WayEnum.CUTE_TIGER_MARKET, rd);
        }
    }

    /**
     * 获得前一天售价
     *
     * @param pastryId
     * @param date
     * @return
     */
    private double getLastDaySellPrice(int pastryId, int date) {
        //获得基础价格
        double pastryInitialPrice = HolidayCuteTigerMarketTool.getPastryInitialPrice(pastryId);
        //获得事件
        List<Integer> specialEventEffects = holidayCuteTigerMarketService.getSpecifyDateBeforeEffects(date, pastryId);
        //计算事件效果
        pastryInitialPrice = sellPriceByEvents(specialEventEffects, pastryInitialPrice);
        return pastryInitialPrice;
    }

    /**
     * 野怪额外产出
     *
     * @param uid
     * @return
     */
    public List<Award> yeGuaiBoxExtraAwards(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return new ArrayList<>();
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            return new ArrayList<>();
        }
        //获得概率
        CfgCuteTigerEntity cuteTigerCfg = HolidayCuteTigerMarketTool.getCuteTigerCfg();
        int yeGuaiTriggerPro = cuteTigerCfg.getYeGuaiTriggerPro();
        if (!PowerRandom.hitProbability(yeGuaiTriggerPro)) {
            return new ArrayList<>();
        }
        //额外奖励
        return HolidayCuteTigerMarketTool.getRandomAwardsByPro(cuteTigerCfg.getYeGuaiBoxOutPut());
    }

    /**
     * 获得村庄产出
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return;
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            return;
        }
        //获得概率
        CfgCuteTigerEntity cuteTigerCfg = HolidayCuteTigerMarketTool.getCuteTigerCfg();
        int cunZTriggerPro = cuteTigerCfg.getCunZTriggerPro();
        if (!PowerRandom.hitProbability(cunZTriggerPro)) {
            return;
        }
        //额外奖励
        List<Award> cunZAwards = HolidayCuteTigerMarketTool.getRandomAwardsByPro(cuteTigerCfg.getCunZOutPut());
        //发放奖励
        awardService.fetchAward(uid, cunZAwards, WayEnum.CUTE_TIGER_MARKET, "", rd);
        rd.setActivityEvenType(0);
    }

    /**
     * 特产交易产出
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> specialExtraAwards(long uid) {
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return citySpecialList;
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            return new ArrayList<>();
        }
        //获得概率
        CfgCuteTigerEntity cuteTigerCfg = HolidayCuteTigerMarketTool.getCuteTigerCfg();
        int specialsTriggerPro = cuteTigerCfg.getSpecialsTriggerPro();
        if (!PowerRandom.hitProbability(specialsTriggerPro)) {
            return citySpecialList;
        }
        //额外奖励
        List<Award> specialAwards = HolidayCuteTigerMarketTool.getRandomAwardsByPro(cuteTigerCfg.getSpecialsOutPut());
        if (!specialAwards.isEmpty()) {
            citySpecialList.add(0, new RDTradeInfo.RDCitySpecial(specialAwards.get(0).gainAwardId(), 0, 0));
        }
        return citySpecialList;
    }

    /**
     * 触发特殊野地事件
     *
     * @param uid
     */
    public void specialYeDiEvent(long uid, RDArriveYeD rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            return;
        }
        CfgCuteTigerEntity cuteTigerCfg = HolidayCuteTigerMarketTool.getCuteTigerCfg();
        //获得事件触发次数
        Integer specialEventTimes = holidayCuteTigerMarketService.getSpecialEventTimes();
        //次数上限
        int maxTimes = cuteTigerCfg.getMaxTriggerspecialYeDiTimes();
        if (specialEventTimes >= maxTimes) {
            return;
        }
        int specialYeDiEventPro = cuteTigerCfg.getSpecialYeDiEventPro();
        if (!PowerRandom.hitProbability(specialYeDiEventPro, 1000)) {
            return;
        }
        //随机事件
        CfgCuteTigerEntity.SpecialYeDiEvent randomSpecialYeDiEvent = HolidayCuteTigerMarketTool.getRandomSpecialYeDiEvent();
        int yeDiEventId = randomSpecialYeDiEvent.getId();
        int pastryId = randomSpecialYeDiEvent.getTreasureId();
        int sellPrice = getSellPrice(pastryId);
        //获得半价
        int discountPrice = (int) (HolidayCuteTigerMarketTool.getPastryInitialPrice(pastryId) * 0.5);
        //如果某个糕点价格已经低于半价，则必定上升
        if (sellPrice < discountPrice && randomSpecialYeDiEvent.getEffect() < 0) {
            yeDiEventId = HolidayCuteTigerMarketTool.getRandomSpecialYeDiGoodEvent(pastryId).getId();
        }
        //获得3倍价格
        discountPrice = HolidayCuteTigerMarketTool.getPastryInitialPrice(pastryId) * 3;
        //如果高于3倍，必定下降
        if (sellPrice > discountPrice && randomSpecialYeDiEvent.getEffect() > 0) {
            yeDiEventId = HolidayCuteTigerMarketTool.getRandomSpecialYeDiBadEvent(pastryId).getId();
        }
        //增加次数
        holidayCuteTigerMarketService.addSpecialEventTimes();
        //增加效果事件
        holidayCuteTigerMarketService.addSpecialEventEffects(uid, yeDiEventId);
        rd.setSpecialYeDEventId(yeDiEventId);
    }

    /**
     * 出售糕点
     *
     * @param uid
     * @param pastryId
     * @return
     */
    public RDCommon sellPastries(long uid, int pastryId) {
        //检查是否拥有
        TreasureChecker.checkHasTreasure(uid, pastryId);
        //获得出售价格
        int sellPrice = getSellPrice(pastryId);
        //获得目前所有糕点
        int treasureNum = userTreasureService.getTreasureNum(uid, pastryId);
        //获得可获得小虎币数量
        int littleTigerCoin = treasureNum * sellPrice;
        //扣除糕点
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, pastryId, treasureNum, WayEnum.CUTE_TIGER_MARKET, rd);
        //增加小虎币
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.LITTLE_TIGER_COIN.getValue(), littleTigerCoin, WayEnum.CUTE_TIGER_MARKET, rd);
        return rd;
    }

    /**
     * 获得当前出售价格
     *
     * @param pastryId
     * @return
     */
    private int getSellPrice(int pastryId) {
        //获得初始价格
        double pastryInitialPrice = HolidayCuteTigerMarketTool.getPastryInitialPrice(pastryId);
        //获得事件
        List<Integer> specialEventEffects = holidayCuteTigerMarketService.getSpecialEventsId(pastryId);
        //计算事件效果
        pastryInitialPrice = sellPriceByEvents(specialEventEffects, pastryInitialPrice);
        return (int) pastryInitialPrice;
    }

    /**
     * 计算事件效果
     *
     * @param specialEventEffects
     * @param pastryInitialPrice
     * @return
     */
    private double sellPriceByEvents(List<Integer> specialEventEffects, double pastryInitialPrice) {
        for (Integer eventId : specialEventEffects) {
            CfgCuteTigerEntity.SpecialYeDiEvent specialYeDiEvent = HolidayCuteTigerMarketTool.getSpecialYeDiEvent(eventId);
            double effect = specialYeDiEvent.getEffect();
            pastryInitialPrice = pastryInitialPrice * (1 + effect);
        }
        return pastryInitialPrice;
    }

    /**
     * 刷新奖励
     *
     * @param uid
     * @param number
     */
    public RDCommon refreshAwards(long uid, int number) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.actived");
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        List<Integer> awardNum = HolidayCuteTigerMarketTool.getLittleTigerStoreEntity().getAwardNum();
        RDRefreshLittleTigerStore rd = new RDRefreshLittleTigerStore();
        if (!awardNum.contains(number)) {
            throw new ExceptionForClientTip("activity.not.valid.choos");
        }
        //检查对应号码是否有奖励
        Award littleTigerStoreAward = holidayCuteTigerMarketService.getLittleTigerStoreAward(uid, number);
        if (littleTigerStoreAward.getAwardId() != 0) {
            //检查小虎币
            int needLittleTigerCoin = HolidayCuteTigerMarketTool.getLittleTigerStoreEntity().getRefreshNeedLittleTigerCoin();
            TreasureChecker.checkIsEnough(TreasureEnum.LITTLE_TIGER_COIN.getValue(), needLittleTigerCoin, uid);
            //扣除小虎币
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.LITTLE_TIGER_COIN.getValue(), needLittleTigerCoin, WayEnum.CUTE_TIGER_MARKET, rd);
        }
        //获得刷新次数
        int refreshTimes = holidayCuteTigerMarketService.getRefreshTimes(uid) + 1;
        //获得对应奖池
        int poolId = HolidayCuteTigerMarketTool.getPoolByRefreshTimes(refreshTimes);
        //获得奖池随机奖励
        Award awardByPoolId = HolidayCuteTigerMarketTool.getAwardByPoolId(poolId);
        Award award;
        if (awardByPoolId.getItem() == AwardEnum.KP.getValue()) {
            //获得随机一张卡池卡牌
            Integer randomCard = getRandomCard(uid, poolId);
            award = new Award(randomCard, AwardEnum.KP, 1);
        } else {
            award = awardByPoolId;
        }
        if (littleTigerStoreAward.getAwardId() == 0) {
            //保存奖励
            holidayCuteTigerMarketService.replaceAward(uid, number, award);
        }
        //更新累计刷新次数奖励
        updateGrandTotalAward(uid, refreshTimes);
        //更新刷新次数
        holidayCuteTigerMarketService.addRefreshTimes(uid);
        rd.setNewAward(RDAward.getInstance(award));
        return rd;
    }

    /**
     * 替换奖励
     *
     * @param uid
     * @param number
     * @param awardId
     * @param item
     * @param num
     */
    public RDSuccess replaceAward(long uid, int number, int awardId, int item, int num) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.actived");
        }
        //最后一天不产出
        if (isLastDay(uid)) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        AwardEnum awardEnum = AwardEnum.fromValue(item);
        if (null == awardEnum) {
            return new RDSuccess();
        }
        Award award = new Award(awardId, awardEnum, num);
        holidayCuteTigerMarketService.replaceAward(uid, number, award);
        return new RDSuccess();
    }

    /**
     * 领取奖励
     *
     * @param uid
     * @param number
     * @return
     */
    public RDCommon receiveAward(long uid, int number) {
        if (!isLastTwoDay(uid)) {
            throw new ExceptionForClientTip("activity.cuteTigerMarket.exchange.not");
        }
        //检查奖励状态
        int status = holidayCuteTigerMarketService.getLittleTigerStoreAwardStatus(uid, number);
        if (status == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        List<Award> awardList = new ArrayList<>();
        Award storeAward = holidayCuteTigerMarketService.getLittleTigerStoreAward(uid, number);
        awardList.add(storeAward);
        //发放奖励
        RDCommon rd = new RDCommon();
        awardService.fetchAward(uid, awardList, WayEnum.CUTE_TIGER_MARKET, "", rd);
        //更新奖励状态
        holidayCuteTigerMarketService.updateLittleTigerStoreAwardStatus(uid, number, AwardStatus.AWARDED.getValue());
        return rd;
    }

    /**
     * 是否是活动最后两天
     *
     * @param uid
     * @return
     */
    protected boolean isLastTwoDay(long uid) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CUTE_TIGER_MARKET);
        long remainTime = getRemainTime(uid, sid, a);
        if (remainTime < 0) {
            return false;
        }
        long twoDayRemainTime = 2 * DateUtil.SECOND_ONE_DAY * 1000;
        if (remainTime <= twoDayRemainTime) {
            return true;
        }
        return false;
    }

    /**
     * 发送累计刷新次数奖励
     *
     * @param uid
     * @param refreshTimes
     */
    public RDCommon sendGrandTotalAward(long uid, int refreshTimes) {
        List<Award> grandTotalAward = HolidayCuteTigerMarketTool.getGrandTotalAward(refreshTimes);
        if (grandTotalAward.isEmpty()) {
            throw new ExceptionForClientTip("activity.unaward");
        }
        //检查领取状态
        int status = holidayCuteTigerMarketService.getGrandTotalAwardStatus(uid, refreshTimes);
        if (status == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        RDCommon rd = new RDCommon();
        //发放奖励
        awardService.fetchAward(uid, grandTotalAward, WayEnum.CUTE_TIGER_MARKET, "", rd);
        //更新状态
        holidayCuteTigerMarketService.updateGrandTotalAwardStatus(uid, refreshTimes, AwardStatus.AWARDED.getValue());
        return rd;
    }

    /**
     * 更新领取状态
     *
     * @param uid
     * @param refreshTimes
     */
    private void updateGrandTotalAward(long uid, int refreshTimes) {
        List<Award> grandTotalAward = HolidayCuteTigerMarketTool.getGrandTotalAward(refreshTimes);
        if (grandTotalAward.isEmpty()) {
            return;
        }
        //检查状态
        int status = holidayCuteTigerMarketService.getGrandTotalAwardStatus(uid, refreshTimes);
        if (status == AwardStatus.ENABLE_AWARD.getValue()) {
            return;
        }
        //更新状态
        holidayCuteTigerMarketService.updateGrandTotalAwardStatus(uid, refreshTimes, AwardStatus.ENABLE_AWARD.getValue());
    }

    /**
     * 获得随机一张卡池中卡牌
     *
     * @param uid
     * @param poolId
     * @return
     */
    private Integer getRandomCard(long uid, int poolId) {
        List<Integer> ownCards = userCardService.getUserCards(uid).stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
        List<Integer> poolAllCards = HolidayCuteTigerMarketTool.getPoolAllCards(poolId);
        List<Integer> cards = poolAllCards.stream().filter(c -> !ownCards.contains(c)).collect(Collectors.toList());
        if (cards.isEmpty()) {
            return PowerRandom.getRandomFromList(poolAllCards);
        }
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 进入小虎商店
     *
     * @param uid
     * @return
     */
    public RDLittleTigerStoreInfo enterLittleTigerStore(long uid) {
        RDLittleTigerStoreInfo rd = new RDLittleTigerStoreInfo();
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return rd;
        }
        //更新奖励可领状态
        updateRefreshAwardsStatus(uid);
        //玩家累计刷新次数
        int refreshTimes = holidayCuteTigerMarketService.getRefreshTimes(uid);
        rd.setTotalRefreshTimes(refreshTimes);
        List<CfgLittleTigerStoreEntity.CumulativeRefreshAwards> grandTotalAward = HolidayCuteTigerMarketTool.getGrandTotalAward();
        //获得累计刷新次数奖励
        List<RDLittleTigerStoreInfo.RefreshAwards> refreshAwards = new ArrayList<>();
        for (CfgLittleTigerStoreEntity.CumulativeRefreshAwards cumulativeAwards : grandTotalAward) {
            RDLittleTigerStoreInfo.RefreshAwards awards = new RDLittleTigerStoreInfo.RefreshAwards();
            int times = cumulativeAwards.getRefreshTimes();
            awards.setNeedRefreshTimes(times);
            List<Award> awardList = cumulativeAwards.getAwards();
            awards.setAward(RDAward.getInstances(awardList));
            Integer status = holidayCuteTigerMarketService.getGrandTotalAwardStatus(uid, times);
            awards.setStatus(status);
            refreshAwards.add(awards);
        }
        List<RDLittleTigerStoreInfo.RefreshAwards> awardsList = refreshAwards.stream()
                .sorted(Comparator.comparing(RDLittleTigerStoreInfo.RefreshAwards::getNeedRefreshTimes)).collect(Collectors.toList());
        rd.setRefreshAwards(awardsList);
        //当前奖励
        List<Integer> awardNum = HolidayCuteTigerMarketTool.getLittleTigerStoreEntity().getAwardNum();
        List<RDLittleTigerStoreInfo.CurrentAwards> currentAwards = new ArrayList<>();
        for (Integer num : awardNum) {
            RDLittleTigerStoreInfo.CurrentAwards awards = new RDLittleTigerStoreInfo.CurrentAwards();
            Award storeAward = holidayCuteTigerMarketService.getLittleTigerStoreAward(uid, num);
            awards.setAward(RDAward.getInstance(storeAward));
            awards.setNumber(num);
            Integer status = holidayCuteTigerMarketService.getLittleTigerStoreAwardStatus(uid, num);
            awards.setStatus(status);
            currentAwards.add(awards);
        }
        rd.setCurrentAwards(currentAwards);
        return rd;
    }

    /**
     * 更新奖励可领状态
     *
     * @param uid
     */
    private void updateRefreshAwardsStatus(long uid) {
        //是否是活动最后一天
        if (!isLastTwoDay(uid)) {
            return;
        }
        List<Integer> awardNum = HolidayCuteTigerMarketTool.getLittleTigerStoreEntity().getAwardNum();
        for (Integer number : awardNum) {
            Integer status = holidayCuteTigerMarketService.getLittleTigerStoreAwardStatus(uid, number);
            if (status != AwardStatus.UNAWARD.getValue()) {
                continue;
            }
            holidayCuteTigerMarketService.updateLittleTigerStoreAwardStatus(uid, number, AwardStatus.ENABLE_AWARD.getValue());
        }
    }


}
