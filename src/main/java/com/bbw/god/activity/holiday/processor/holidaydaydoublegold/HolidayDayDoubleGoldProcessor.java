package com.bbw.god.activity.holiday.processor.holidaydaydoublegold;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 首冲双倍活动(元宝福利)
 *
 * @author: huanghb
 * @date: 2022/12/27 15:18
 */
@Service
public class HolidayDayDoubleGoldProcessor extends AbstractActivityProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HolidayUserDayDoubleGoldService holidayUserDayDoubleGoldService;

    public HolidayDayDoubleGoldProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.DOUBLE_GOLD);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.DOUBLE_GOLD.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 是否首充双倍
     *
     * @param uid
     */
    public boolean isFirstGoldPayDouble(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return false;
        }
        HolidayUserDayDoubleGold holidayUserDayDoubleGold = holidayUserDayDoubleGoldService.getHolidayUserDayDoubleGold(uid);
        if (0 == holidayUserDayDoubleGold.getBuyNum()) {
            return true;
        }
        return false;
    }

    /**
     * 添加购买次数
     */
    public void addBuyNum(long uid) {
        HolidayUserDayDoubleGold holidayUserDayDoubleGold = holidayUserDayDoubleGoldService.getHolidayUserDayDoubleGold(uid);
        holidayUserDayDoubleGold.addBuyNum();
        holidayUserDayDoubleGoldService.addData(holidayUserDayDoubleGold);
    }
}
