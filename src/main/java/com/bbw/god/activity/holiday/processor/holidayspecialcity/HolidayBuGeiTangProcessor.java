package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.rd.RDActivityArriveBox;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 不给糖就捣乱
 *
 * @author fzj
 * @date 2021/10/20 15:59
 */
@Service
public class HolidayBuGeiTangProcessor extends AbstractSpecialCityProcessor {
    @Autowired
    PrivilegeService privilegeService;

    private static String BU_GEI_TANG_GUAI_WEI = "不给糖就捣乱怪味糖豆";

    public HolidayBuGeiTangProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.BU_GEI_TANG);
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
     * 触发游商馆事件
     *
     * @param uid
     * @param rd
     */
    @Override
    public void youSGTriggerEvent(long uid, RDAdvance rd) {
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
        int seed = getRandom();
        int pro = 300;
        if (seed <= pro) {
            return;
        }
        // 70% 触发活动事件
        doEventTrigger(uid, rd);
    }

    /**
     * 执行活动事件
     *
     * @param uid
     * @param rd
     */
    private void doEventTrigger(long uid, RDAdvance rd) {
        int seed = getRandom();
        int pro = 700;
        if (seed <= pro) {
            //开宝箱
            rd.setActivityEvenType(0);
            TimeLimitCacheUtil.removeCache(uid, RDActivityArriveBox.class);
            GameUser gu = gameUserService.getGameUser(uid);
            //地灵印额外赠加的次数
            int extraFightBoxFreeTimes = privilegeService.getExtraFightBoxFreeTimes(gu);
            RDActivityArriveBox rdActivityArriveBox = RDActivityArriveBox.getInstance(3, extraFightBoxFreeTimes + 1, 5);
            TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
            return;
        }
        //挑战南瓜幽灵
        rd.setActivityEvenType(1);
    }

    /**
     * 活动开宝箱
     *
     * @param uid
     * @return
     */
    public RDCommon openBox(long uid) {
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
            needGold += 5;
            rdActivityArriveBox.setOpenBoxNeedGolds(needGold);
        }
        int awardId = getAwardId();
        if (awardId != 0) {
            awardId = checkGuaiWeiTangNum(uid, awardId);
            TreasureEventPublisher.pubTAddEvent(gu.getId(), awardId, 1, WayEnum.WAN_S_ACTIVITY_BOX, rd);
        } else {
            CardEventPublisher.pubCardAddEvent(gu.getId(), 438, WayEnum.WAN_S_ACTIVITY_BOX, "在不给糖就捣乱活动中获得", rd);
        }
        rdActivityArriveBox.setFreeTimes(freeTime);
        rdActivityArriveBox.setRemainTimes(remainTime);
        TimeLimitCacheUtil.setArriveCache(uid, rdActivityArriveBox);
        rd.setFreeTimes(freeTime);
        return rd;
    }

    private int getAwardId() {
        int seed = getRandom();
        if (seed <= 590) {
            return TreasureEnum.XIONG_XRT.getValue();
        }
        if (seed > 590 && seed <= 980) {
            return TreasureEnum.TANG_SBG.getValue();
        }
        if (seed > 980 && seed <= 998) {
            return TreasureEnum.GUAI_WTD.getValue();
        }
        return 0;
    }

    private int getRandom() {
        return PowerRandom.getRandomBySeed(1000);
    }

    /**
     * 检查怪味糖豆数量如果超过则换成糖霜饼干
     *
     * @param awardId
     * @return
     */
    public int checkGuaiWeiTangNum(long uid, int awardId) {
        Integer guaiWeiTangNum = TimeLimitCacheUtil.getFromCache(uid, BU_GEI_TANG_GUAI_WEI, Integer.class);
        guaiWeiTangNum = null == guaiWeiTangNum ? 0 : guaiWeiTangNum;
        if (guaiWeiTangNum >= 100) {
            return TreasureEnum.TANG_SBG.getValue();
        }
        if (awardId == TreasureEnum.GUAI_WTD.getValue()) {
            TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, BU_GEI_TANG_GUAI_WEI, guaiWeiTangNum + 1, DateUtil.SECOND_ONE_DAY * 10);
        }
        return awardId;
    }

}
