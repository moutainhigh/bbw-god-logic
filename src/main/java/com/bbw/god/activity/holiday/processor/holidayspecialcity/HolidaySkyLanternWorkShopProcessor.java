package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayCunZEventAwardEntity;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidaySkyLanternWorkShopMallProcessor;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 天灯工坊
 *
 * @author: huanghb
 * @date: 2022/2/9 14:30
 */
@Service
public class HolidaySkyLanternWorkShopProcessor extends AbstractSpecialCityProcessor implements IChengChiTradeService {
    @Autowired
    private HolidaySkyLanternWorkShopMallProcessor holidaySkyLanternWorkShopMallProcessor;
    @Autowired
    private AwardService awardService;
    /** 节日额外奖励 */
    public final static List<Integer> HOLIDAY_EXTRA_AWARDS = Arrays.asList(
            TreasureEnum.WISHING_CANDLE.getValue(),
            TreasureEnum.WOOD_WIRE.getValue(),
            TreasureEnum.TISSUE.getValue());
    /** 购买道具需要的铜钱 */
    private static final Integer BUY_NEED_COPPER = 5000;
    /** 获得活动特产概率 */
    private static final int GET_SPECIAL_PROB = 35;

    public HolidaySkyLanternWorkShopProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.SKY_LANTERN_WORKSHOP);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }


    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        int sid = gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDMallList malls = holidaySkyLanternWorkShopMallProcessor.getGoods(uid);
        RDActivityList rd = new RDActivityList();
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, a));
        return rd;
    }

    /**
     * 野怪宝箱节日额外奖励
     *
     * @param uid
     */
    public Award getExtraAwardByYGBox(long uid) {
        //活动是否开启
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return null;
        }
        //添加额外奖励
        List<Integer> awardIds = PowerRandom.getRandomsFromList(HOLIDAY_EXTRA_AWARDS, 1);
        return new Award(awardIds.get(0), AwardEnum.FB, 1);
    }

    /**
     * 村庄拜访节日额外奖励
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        int pro = 40;
        //是否获的额外奖励
        if (!PowerRandom.hitProbability(pro)) {
            return;
        }
        //活动是否开启
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        //添加额外奖励
        List<Award> awards = CfgHolidayCunZEventAwardEntity.randomAwardByProb(ActivityEnum.SKY_LANTERN_WORKSHOP.getValue());
        awardService.fetchAward(uid, awards, WayEnum.SKY_LANTERN_WORKSHOP, WayEnum.SKY_LANTERN_WORKSHOP.getName(), rd);
        rd.setActivityEvenType(0);

    }

    /**
     * 城池特产节日额外奖励
     *
     * @param uid
     */
    public List<RDTradeInfo.RDCitySpecial> specialExtraAwards(long uid) {
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return citySpecialList;
        }
        if (!PowerRandom.hitProbability(GET_SPECIAL_PROB)) {
            return citySpecialList;
        }
        List<Integer> awardIds = PowerRandom.getRandomsFromList(HOLIDAY_EXTRA_AWARDS, 1);
        for (Integer awardId : awardIds) {
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(awardId, 0, 0));
        }
        return citySpecialList;
    }

    /**
     * 获取子服务可购买的物品
     *
     * @return
     */
    @Override
    public List<Integer> getAbleTradeGoodIds() {
        return HOLIDAY_EXTRA_AWARDS;
    }

    /**
     * 获取要购买的物品价格
     *
     * @param goodId
     * @return
     */
    @Override
    public int getTradeBuyPrice(int goodId) {
        return BUY_NEED_COPPER;
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
