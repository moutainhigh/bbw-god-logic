package com.bbw.god.activity.holiday.processor.holidaytreasuretrove;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.mall.processor.MallProcessorFactory;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 节日活动藏宝秘境
 *
 * @author: huanghb
 * @date: 2021/12/17 8:53
 */
@Service
public class HolidayTreasureTroveProcessor extends AbstractActivityProcessor {
    @Autowired
    private MallProcessorFactory mallProcessorFactory;

    public HolidayTreasureTroveProcessor() {

        this.activityTypeList = Arrays.asList(ActivityEnum.TREASURE_SECRET);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 活动剩余时间
     *
     * @param uid
     * @param sid
     * @param activity
     * @return
     */
    @Override
    public long getRemainTime(long uid, int sid, IActivity activity) {
        if (activity.gainEnd() != null) {
            return activity.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }


    /**
     * 获取活动信息
     *
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long guId, int activityType) {
        int sid = this.gameUserService.getActiveSid(guId);
        if (!isOpened(sid, activityType)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDTreasureSectetInfos rd = (RDTreasureSectetInfos) this.mallProcessorFactory.getMallProcessor(MallEnum.TREASURE_SECRET.getValue()).getGoods(guId);
        IActivity activity = activityService.getActivity(sid, ActivityEnum.fromValue(activityType));
        rd.setRemainTime(getRemainTime(guId, sid, activity));
        rd.setCurType(activityType);
        return rd;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @param type
     * @return
     */
    public boolean isOpened(int sid, int type) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(type);
        IActivity activity = activityService.getActivity(sid, activityEnum);
        return activity != null;
    }
}

