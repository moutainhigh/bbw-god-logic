package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.activity.rd.RDDiscount;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.biggodplan.event.CombinedServiceDiscountEventPublisher;
import com.bbw.god.mall.RDMallInfo;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.CombinedServiceDiscountChangeMallProcessor;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 合服折扣变化商店
 *
 * @author: huanghb
 * @date: 2022/2/17 14:35
 */
@Service
public class CombinedServiceDiscountChangeProcessor extends AbstractActivityProcessor {


    @Autowired
    private CombinedServiceDiscountChangeMallProcessor combinedServiceDiscountChangeMallProcessor;
    /** 限时折扣redisKey */
    private static final String LIMITED_TIME_DISCOUNT = "合服-限时折扣";
    /** 十折即无打折优惠 */
    private static final int SHI_DISCOUNT = 1;
    /** 五折 */
    private static final double WU_DISCOUNTD = 0.5;
    /** 五折概率 */
    private static final double WU_DISCOUNTD_PROB = 10;
    /** 第一次六折概率 */
    private static final double FIRST_LIU_DISCOUNTD_PROB = 65;
    /** 六折概率 */
    private static final double LIU_DISCOUNTD_PROB = 30;
    /** 六折 */
    private static final double LIU_DISCOUNTD = 0.6;
    /** 七折 */
    private static final double QI_DISCOUNTD = 0.5;
    /** 打折需要元宝 */
    private static final int DISCOUNT_NEED_GOLD = 1;
    /** 折扣总概率 */
    private static final int DISCOUNT_TOTAL_PROB = 100;
    /** 缓存天数 */
    private static final int CACHE_DAYS = 10;


    public CombinedServiceDiscountChangeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.COMBINED_SERVICE_DISCOUNT);
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
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDMallList malls = combinedServiceDiscountChangeMallProcessor.getGoods(uid);
        //获取商品折扣
        for (RDMallInfo rdMallInfo : malls.getMallGoods()) {
            rdMallInfo.setDiscount(getLimitedTimeDiscount(uid));
        }
        rd.setRdMallList(malls);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.COMBINED_SERVICE_DISCOUNT);
        rd.setRemainTime(getRemainTime(uid, sid, a));
        return rd;
    }

    /**
     * 刷新或者获得初始折扣
     *
     * @param uid
     * @return
     */
    public RDDiscount getOrRefreshDiscount(long uid) {
        Double discount = getLimitedTimeDiscount(uid);
        RDDiscount rd = new RDDiscount();
        if (SHI_DISCOUNT == discount) {
            //获得初始折扣
            discount = getInitialDiscount();
            BaseEventParam bep = new BaseEventParam(uid, WayEnum.COMBINED_SERVICE_DISCOUNT, rd);
            CombinedServiceDiscountEventPublisher.combinedServiceDiscountEvent(discount, bep);
            TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, LIMITED_TIME_DISCOUNT, discount, DateUtil.SECOND_ONE_DAY * CACHE_DAYS);
            rd.setDiscount(discount);
            return rd;
        }
        //折扣为五折时不能刷新折扣
        if (WU_DISCOUNTD == discount) {
            rd.setDiscount(discount);
            return rd;
        }
        //刷新折扣
        GameUser gu = gameUserService.getGameUser(uid);
        ResChecker.checkGold(gu, DISCOUNT_NEED_GOLD);
        // 扣除元宝
        ResEventPublisher.pubGoldDeductEvent(uid, DISCOUNT_NEED_GOLD, WayEnum.COMBINED_SERVICE_DISCOUNT, rd);
        //获得新折扣
        discount = getRefreshDiscount();
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, LIMITED_TIME_DISCOUNT, discount, DateUtil.SECOND_ONE_DAY * CACHE_DAYS);
        rd.setDiscount(discount);
        return rd;
    }

    /**
     * 获取限时折扣
     *
     * @param uid
     * @return
     */
    public Double getLimitedTimeDiscount(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        Double discount = TimeLimitCacheUtil.getFromCache(uid, LIMITED_TIME_DISCOUNT, Double.class);
        return null == discount ? SHI_DISCOUNT : discount;
    }

    /**
     * 根据概率获取初始折扣
     *
     * @return
     */
    private double getInitialDiscount() {
        int randomBySeed = PowerRandom.getRandomBySeed(DISCOUNT_TOTAL_PROB);
        if (randomBySeed <= WU_DISCOUNTD_PROB) {
            return WU_DISCOUNTD;
        }
        if (randomBySeed <= LIU_DISCOUNTD_PROB) {
            return LIU_DISCOUNTD;
        }
        return QI_DISCOUNTD;
    }

    /**
     * 根据概率获取刷新折扣
     *
     * @return
     */
    private double getRefreshDiscount() {
        int randomBySeed = PowerRandom.getRandomBySeed(DISCOUNT_TOTAL_PROB);
        if (randomBySeed <= FIRST_LIU_DISCOUNTD_PROB) {
            return LIU_DISCOUNTD;
        }
        return WU_DISCOUNTD;

    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    private boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.COMBINED_SERVICE_DISCOUNT.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }
}
