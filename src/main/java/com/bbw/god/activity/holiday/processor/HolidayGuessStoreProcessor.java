package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidayGuessingStoreMallProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 竞猜商店
 *
 * @author longwh
 * @date 2022/11/11 10:54
 */
@Service
public class HolidayGuessStoreProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayGuessingStoreMallProcessor mallProcessor;

    public HolidayGuessStoreProcessor() {
        this.activityTypeList = Collections.singletonList(ActivityEnum.WORLD_CUP_ACTIVITIE_GUESS_SHOP);
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

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.WORLD_CUP_ACTIVITIE_GUESS_SHOP.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        //活动是否开启
        int sid = gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //获得竞猜商店商品
        RDMallList malls = mallProcessor.getGoods(uid);
        //获得活动实例
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        //返回竞猜商店物品
        RDActivityList rd = new RDActivityList();
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, a));
        return rd;
    }

    /**
     * 获得活动剩余时间
     *
     * @param uid
     * @param sid
     * @param a
     * @return
     */
    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}