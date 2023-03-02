package com.bbw.god.activity.holiday.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.lottery.HolidayWQCYTool;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayLaborGloriousProcessor;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.city.chengc.trade.BuyGoodInfo;
import com.bbw.god.city.chengc.trade.IChengChiTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 活动抽奖处理器
 * @date 2020/9/1 14:32
 **/
@Service
public class HolidayLotteryProcessor extends AbstractActivityProcessor implements IChengChiTradeService {
    @Autowired
    HolidayLaborGloriousProcessor holidayLaborGloriousProcessor;
    /** 圣元珠Id集合 */
    public static final List<Integer> SHENG_YUAN_ZHU_IDS = Arrays.asList(11440, 11450, 11460, 11470, 11480);
    /** 够买圣元珠需要的铜钱 */
    private static final Integer BUY_NEED_COPPER = 5000;

    public HolidayLotteryProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_LOTTERY, ActivityEnum.HOLIDAY_BO_BING, ActivityEnum.WQCY, ActivityEnum.LIMIT_TIME_DRAW_FCJB, ActivityEnum.LIMIT_TIME_DRAW_FCJB_51, ActivityEnum.HOLIDAY_JHNF);
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
        return SHENG_YUAN_ZHU_IDS;
    }

    @Override
    public int getTradeBuyPrice(int goodId) {
        return BUY_NEED_COPPER;
    }

    @Override
    public List<BuyGoodInfo> getTradeBuyInfo(long uid, List<Integer> specialIds) {
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return new ArrayList<>();
        }
        if (holidayLaborGloriousProcessor.isOpened(sid)) {
            return new ArrayList<>();
        }
        return IChengChiTradeService.super.getTradeBuyInfo(uid, specialIds);
    }


    /**
     * 五气朝元-特产交易产出
     *
     * @param uid
     * @return
     */
    public List<RDTradeInfo.RDCitySpecial> specialExtraAwards(long uid) {
        List<RDTradeInfo.RDCitySpecial> citySpecialList = new ArrayList<>();
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            return new ArrayList<>();
        }
        if (holidayLaborGloriousProcessor.isOpened(sid)) {
            return new ArrayList<>();
        }
        //获得概率100%
        int totalProb = HolidayWQCYTool.getSpecialsOutPutTotalProb();
        if (0 == totalProb) {
            return citySpecialList;
        }
        int randomBySeed = PowerRandom.getRandomBySeed(totalProb);        //20%概率获得一颗圣灵珠
        if (randomBySeed <= HolidayWQCYTool.getSpecialsOutPutOneSYZProb()) {
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(PowerRandom.getRandomFromList(SHENG_YUAN_ZHU_IDS), 0, 0));
            return citySpecialList;
        }
        //3%概率获得两颗圣灵珠
        if (randomBySeed <= HolidayWQCYTool.getSpecialsOutPutTwoSYZProb()) {
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(PowerRandom.getRandomFromList(SHENG_YUAN_ZHU_IDS), 0, 0));
            citySpecialList.add(new RDTradeInfo.RDCitySpecial(PowerRandom.getRandomFromList(SHENG_YUAN_ZHU_IDS), 0, 0));
            return citySpecialList;
        }
        //70%什么都没有，直接返回
        return citySpecialList;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.WQCY.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

}
