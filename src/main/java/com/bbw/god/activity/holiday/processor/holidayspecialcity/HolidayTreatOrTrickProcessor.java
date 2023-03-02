package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.config.CfgHolidayTreatOrTrick;
import com.bbw.god.activity.holiday.config.HolidayHalloweenRestaurantOrder;
import com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant.HolidayHalloweenRestaurantDateService;
import com.bbw.god.activity.rd.RDActivityArriveBox;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 不给糖就捣乱2
 *
 * @author: huanghb
 * @date: 2022/10/10 17:04
 */
@Service
public class HolidayTreatOrTrickProcessor<RedisMapHash> extends AbstractSpecialCityProcessor {
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    private HolidayHalloweenRestaurantDateService holidayHalloweenRestaurantDateService;
    /** 订单key */
    private static String TREAT_OR_TRICK_ORDER = "不给糖就捣乱订单";
    /** 缓存1小时超时 */
    private static final Long REDIS_TIME_OUT = 1 * 60 * 60L;

    public HolidayTreatOrTrickProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.TREAT_OR_TRICK);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 触发村庄事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
        checkAndTrigger(uid, rd);
    }


    /**
     * 触发活动事件
     *
     * @param rd
     */
    public void checkAndTrigger(long uid, RDAdvance rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        boolean isTriggerCunZEvent = PowerRandom.hitProbability(CfgHolidayTreatOrTrick.getCfg().getCunZTriggerProb());
        if (!isTriggerCunZEvent) {
            return;
        }
        // 触发活动事件
        doEventTrigger(uid, rd);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    private void doEventTrigger(long uid, RDAdvance rd) {
        boolean isTriggerAward = PowerRandom.hitProbability(CfgHolidayTreatOrTrick.getCfg().getBoxTriggerProb());
        if (isTriggerAward) {
            //直接获得奖励
            rd.setActivityEvenType(0);
            TimeLimitCacheUtil.removeCache(uid, RDActivityArriveBox.class);
            //到达缓存
            RDActivityArriveBox rdActivityArriveBox = RDActivityArriveBox.getInstance(1, 1, 0);
            TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
            return;
        }
        //获得订单
        rd.setActivityEvenType(1);
        HolidayHalloweenRestaurantOrder order = CfgHolidayTreatOrTrick.getOrder();
        holidayHalloweenRestaurantDateService.updateOrder(uid, order);
        return;
    }

    /**
     * 活动开宝箱
     *
     * @param uid
     * @return
     */
    public RDCommon openBox(long uid) {
        RDActivityArriveBox rdActivityArriveBox = TimeLimitCacheUtil.getArriveCache(uid, RDActivityArriveBox.class);
        //获得剩余次数
        int remainTime = rdActivityArriveBox.getRemainTimes();
        //是否还有剩余次数
        if (remainTime == 0) {
            throw new ExceptionForClientTip("yg.openbox.not.remain");
        }
        //扣除剩余次数
        remainTime--;
        RDCommon rd = new RDCommon();
        //获得开箱需要法宝
        Integer boxReceiveNeedTreasure = CfgHolidayTreatOrTrick.getCfg().getBoxReceiveNeedTreasure();
        //获得开箱需要法宝数量
        Integer boxReceiveNeedTreasureNum = CfgHolidayTreatOrTrick.getCfg().getBoxReceiveNeedTreasureNum();
        //检查法宝是否是否足够
        TreasureChecker.checkIsEnough(boxReceiveNeedTreasure, boxReceiveNeedTreasureNum, uid);
        //扣除法宝
        TreasureEventPublisher.pubTDeductEvent(uid, boxReceiveNeedTreasure, boxReceiveNeedTreasureNum, WayEnum.TREAT_OR_TRICK, rd);
        //获得开箱奖励
        Award outPut = CfgHolidayTreatOrTrick.randomAwardByProb(CfgHolidayTreatOrTrick.getCfg().getBoxOutPut());

        awardService.fetchAward(uid, Arrays.asList(outPut), WayEnum.TREAT_OR_TRICK, "", rd);

        rdActivityArriveBox.setRemainTimes(remainTime);
        //更新缓存
        TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
        return rd;
    }

}
