package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.cfg.CfgCuteTigerEntity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidaySummerHeat;
import com.bbw.god.activity.holiday.processor.holidaycutetugermarket.HolidayCuteTigerMarketTool;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.ActivityRandomExchangeMallProcessor;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日-(威震九州,無啥代志)
 *
 * @author: huanghb
 * @date: 2022/1/6 11:08
 */
@Service
public class HolidayWZJZProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
    @Autowired
    private ActivityRandomExchangeMallProcessor activityRandomExchangeMallProcessor;

    public HolidayWZJZProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_WZJZ);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }


    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        //活动是否开启
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //获得物品
        RDMallList malls = activityRandomExchangeMallProcessor.getGoods(uid);
        //活动实例
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        //返回
        RDActivityList rd = new RDActivityList();
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, a));
        return rd;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 触发活动事件
     *
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        //活动是否开启
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        //是否触发活动事件
        int seed = getRandom();
        int cunzProb = CfgHolidaySummerHeat.getCfg().getCunZTriggerPro();
        if (seed > cunzProb) {
            return;
        }
        // 25% 触发活动事件
        doEventTrigger(uid, rd);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    private void doEventTrigger(long uid, RDAdvance rd) {
        rd.setActivityEvenType(0);
        Award outPut = CfgHolidaySummerHeat.randomAwardByProb(CfgHolidaySummerHeat.getCfg().getCunZOutPut());
        TreasureEventPublisher.pubTAddEvent(uid, outPut.getAwardId(), outPut.getNum(), WayEnum.HOLIDAY_WZJZ, rd);
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
        //获得概率
        int yeGuaiTriggerPro = CfgHolidaySummerHeat.getCfg().getYeGuaiTriggerPro();
        if (!PowerRandom.hitProbability(yeGuaiTriggerPro)) {
            return new ArrayList<>();
        }
        //额外奖励
        List<Award> awards = HolidayCuteTigerMarketTool.getRandomAwardsByPro(CfgHolidaySummerHeat.getCfg().getYeGuaiBoxOutPut());
        return awards;
    }

    /**
     * 获得随机数
     *
     * @return
     */
    private int getRandom() {
        return PowerRandom.getRandomBySeed(100);
    }

    /**
     * 获取子服务可购买的物品
     *
     * @return
     */
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return CfgHolidaySummerHeat.getCfg().getInitialSellingPrice()
                .stream()
                .map(CfgCuteTigerEntity.InitialSellingPrice::getTreasureId)
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
        CfgCuteTigerEntity.InitialSellingPrice initialSellingPrice = CfgHolidaySummerHeat.getCfg()
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
        //没有获得特产
        if (!PowerRandom.hitProbability(CfgHolidaySummerHeat.getCfg().getSpecialsTriggerPro())) {
            return citySpecialList;
        }
        //获得特产
        Award outPut = CfgHolidaySummerHeat.randomAwardByProb(CfgHolidaySummerHeat.getCfg().getSpecialsOutPut());
        citySpecialList.add(new RDTradeInfo.RDCitySpecial(outPut.getAwardId(), 0, 0));
        return citySpecialList;
    }
}
