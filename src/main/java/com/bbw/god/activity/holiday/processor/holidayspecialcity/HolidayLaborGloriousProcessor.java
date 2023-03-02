package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayKoiPray;
import com.bbw.god.activity.holiday.config.CfgHolidayLaborGlorious;
import com.bbw.god.activity.holiday.config.HolidayLarGloriousTool;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketTool;
import com.bbw.god.activity.rd.RDActivityArriveBox;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 劳动光荣活动实现类
 *
 * @author fzj
 * @date 2022/4/13 15:24
 */
@Service
public class HolidayLaborGloriousProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
    @Autowired
    PrivilegeService privilegeService;

    public HolidayLaborGloriousProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.LABOR_GLORIOUS);
    }

    /**
     * 触发村庄事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        CfgHolidayLaborGlorious cfg = HolidayLarGloriousTool.getCfg();
        checkAndTrigger(uid, rd, cfg.getCunZEventTriggerPro(), cfg.getCunZEventBoxTriggerPro(), cfg.getCunZIsDirectOutput(), cfg.getCunZEventBoxOutput());
    }

    /**
     * 触发游商馆事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void youSGTriggerEvent(long uid, RDAdvance rd) {
        CfgHolidayLaborGlorious cfg = HolidayLarGloriousTool.getCfg();
        checkAndTrigger(uid, rd, cfg.getYouSGEventTriggerPro(), cfg.getYouSGEventBoxTriggerPro(), cfg.getYouSGIsDirectOutput(), cfg.getYouSGEventBoxOutput());
    }

    /**
     * 触发客栈事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void keZTriggerEvent(long uid, RDAdvance rd) {
        CfgHolidayLaborGlorious cfg = HolidayLarGloriousTool.getCfg();
        checkAndTrigger(uid, rd, cfg.getKeZEventTriggerPro(), cfg.getKeZEventBoxTriggerPro(), cfg.getKeZIsDirectOutput(), cfg.getKeZEventBoxOutput());
    }

    /**
     * 触发活动事件
     *
     * @param rd
     */
    public void checkAndTrigger(long uid, RDAdvance rd, int eventTriggerPro, int triggerBoxPro, int IsDirectOutPut, List<Award> outPut) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        int seed = PowerRandom.getRandomBySeed(HolidayLarGloriousTool.getEventTotalPro());
        if (seed > eventTriggerPro) {
            return;
        }
        //触发活动事件
        doEventTrigger(uid, rd, triggerBoxPro, IsDirectOutPut, outPut);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    private void doEventTrigger(long uid, RDAdvance rd, int triggerBoxPro, int isDirectOutPut, List<Award> outPut) {
        int seed = PowerRandom.getRandomBySeed(HolidayLarGloriousTool.getEventTotalPro());
        if (seed <= triggerBoxPro) {
            boxOutPut(uid, rd, isDirectOutPut, outPut);
            return;
        }
        //挑战捣乱的地痞
        rd.setActivityEvenType(1);
    }

    /**
     * 获得宝箱产出
     *
     * @param uid
     * @param rd
     * @param isDirectOutPut
     */
    private void boxOutPut(long uid, RDAdvance rd, int isDirectOutPut, List<Award> outPut) {
        rd.setActivityEvenType(0);
        //是否直接获得宝箱产出
        if (0 == isDirectOutPut) {
            Award boxOutPut = CfgHolidayKoiPray.randomAwardByProb(outPut);
            TreasureEventPublisher.pubTAddEvent(uid, boxOutPut.getAwardId(), boxOutPut.getNum(), WayEnum.KOI_PRAY, rd);
            return;
        }
        //开宝箱
        TimeLimitCacheUtil.removeCache(uid, RDActivityArriveBox.class);
        GameUser gu = gameUserService.getGameUser(uid);
        //地灵印额外赠加的次数
        int extraFightBoxFreeTimes = privilegeService.getExtraFightBoxFreeTimes(gu);
        RDActivityArriveBox rdActivityArriveBox = RDActivityArriveBox.getInstance(3, extraFightBoxFreeTimes + 1, 5);
        TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
        return;
    }

    /**
     * 野怪额外产出
     *
     * @param uid
     * @return
     */
    public List<Award> yeGuaiBoxExtraAwards(long uid, int yeGiaiType) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return new ArrayList<>();
        }
        int randomBySeed = PowerRandom.getRandomBySeed(100);
        //普通野怪产出
        if (YeGuaiEnum.YG_NORMAL.getType() == yeGiaiType) {
            //获得概率
            CfgHolidayLaborGlorious cfg = HolidayLarGloriousTool.getCfg();
            int normalYeGuaiBoxTriggerPro = cfg.getNormalYeGuaiBoxTriggerPro();
            if (!(randomBySeed <= normalYeGuaiBoxTriggerPro)) {
                return new ArrayList<>();
            }
            //额外奖励
            return HolidayCuteTigerMarketTool.getRandomAwardsByPro(cfg.getNormalYeGuaiBoxOutput());
        }
        //精英野怪产出
        if (YeGuaiEnum.YG_ELITE.getType() == yeGiaiType) {
            //获得概率
            CfgHolidayLaborGlorious cfg = HolidayLarGloriousTool.getCfg();
            int eliteYeGuaiBoxTriggerPro = cfg.getEliteYeGuaiBoxTriggerPro();
            if (!(randomBySeed <= eliteYeGuaiBoxTriggerPro)) {
                return new ArrayList<>();
            }
            //额外奖励
            List<Award> firstaward = HolidayCuteTigerMarketTool.getRandomAwardsByPro(cfg.getEliteYeGuaiBoxOutput());
            List<Award> awards = new ArrayList<>();
            awards.addAll(firstaward);
            List<Award> secondAward = HolidayCuteTigerMarketTool.getRandomAwardsByPro(cfg.getEliteYeGuaiBoxOutput());
            awards.addAll(secondAward);
            return awards;
        }
        return new ArrayList<>();
    }

    /**
     * 活动开宝箱
     *
     * @param uid
     * @return
     */
    public RDCommon openBox(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.is.timeout");
        }
        RDActivityArriveBox rdActivityArriveBox = TimeLimitCacheUtil.getArriveCache(uid, RDActivityArriveBox.class);
        int remainTime = rdActivityArriveBox.getRemainTimes();
        if (remainTime == 0) {
            throw new ExceptionForClientTip("yg.openbox.not.remain");
        }
        GameUser gu = gameUserService.getGameUser(uid);
        int freeTime = rdActivityArriveBox.getFreeTimes();
        int needGold = rdActivityArriveBox.getOpenBoxNeedGolds();
        if (freeTime > 0) {
            needGold = 0;
            freeTime--;
        }
        remainTime--;
        RDCommon rd = new RDCommon();
        if (needGold > 0) {
            ResChecker.checkGold(gu, needGold);
            ResEventPublisher.pubGoldDeductEvent(uid, needGold, getWay(), rd);
            ActionStatisticTool.addUserActionStatistic(uid, 1, getWay().getName());
            needGold += HolidayLarGloriousTool.getPricePerIncrease();
            rdActivityArriveBox.setOpenBoxNeedGolds(needGold);
        }
        awardService.fetchAward(gu.getId(), Arrays.asList(getAward()), WayEnum.LABOR_BOX, "", rd);
        rdActivityArriveBox.setFreeTimes(freeTime);
        rdActivityArriveBox.setRemainTimes(remainTime);
        TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
        rd.setFreeTimes(freeTime);
        return rd;
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
        int randomBySeed = PowerRandom.getRandomBySeed(100);
        List<Award> specialsOutput = HolidayLarGloriousTool.getSpecialsOutput();
        //概率获得一个特产
        if (randomBySeed <= HolidayLarGloriousTool.getSpecialOutPutOneSpecialProb()) {
            Award award = HolidayLarGloriousTool.randomAwardByProb(specialsOutput);
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(award.getAwardId(), 0, 0));
            return citySpecialList;
        }
        //概率获得两个特产
        if (randomBySeed <= HolidayLarGloriousTool.getSpecialOutPutTwoSpecialProb()) {
            Award firstaward = HolidayLarGloriousTool.randomAwardByProb(specialsOutput);
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(firstaward.getAwardId(), 0, 0));
            Award secondAward = HolidayLarGloriousTool.randomAwardByProb(specialsOutput);
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(secondAward.getAwardId(), 0, 0));
            return citySpecialList;
        }
        //没有获得特产
        return citySpecialList;
    }

    /**
     * 根据概率获得奖励id
     *
     * @return
     */
    private Award getAward() {
        List<Award> awards = HolidayLarGloriousTool.getCunZEventBoxOutput();
        List<Integer> proList = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        int index = PowerRandom.getIndexByProbs(proList, HolidayLarGloriousTool.getEventTotalPro());
        return awards.get(index);
    }

    /**
     * 获取子服务可购买的物品
     *
     * @return
     */
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return HolidayLarGloriousTool.getInitialSellingPrice()
                .stream()
                .map(CfgHolidayLaborGlorious.InitialSellingPrice::getTreasureId)
                .collect(Collectors.toList());
    }

    /**
     * 获取要购买的物品价格
     *
     * @param goodId
     * @return
     */
    @Override
    public int getTradeBuyPrice(int goodId) {
        CfgHolidayLaborGlorious.InitialSellingPrice initialSellingPrice = HolidayLarGloriousTool
                .getInitialSellingPrice()
                .stream()
                .filter(tmp -> tmp.getTreasureId() == goodId).findFirst().orElse(null);
        if (null == initialSellingPrice) {
            return 0;
        }
        return initialSellingPrice.getPrice();
    }

    /**
     * 获取某个批次中某个子服务的物品购买信息
     *
     * @param uid
     * @param specialIds
     * @return
     */
    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return new ArrayList<>();
        }
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }
}
