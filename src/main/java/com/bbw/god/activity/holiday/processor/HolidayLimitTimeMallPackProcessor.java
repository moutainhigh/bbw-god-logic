package com.bbw.god.activity.holiday.processor;

import com.bbw.common.JSONUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lwb
 * @description: 商铺节日限时礼包
 **/
@Service
public class HolidayLimitTimeMallPackProcessor extends AbstractActivityProcessor {

    public HolidayLimitTimeMallPackProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.LIMIT_TIME_MALL_PACK, ActivityEnum.LIMIT_TIME_MALL_PACK_51);
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
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean opened(int sid, ActivityEnum activityEnum) {
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            return false;
        }
        return a.ifTimeValid();
    }

    /***
     * 根据活动表的ID 获取奖励
     * @param id
     * @return
     */
    public List<Award> getAwards(int id, ActivityEnum ActivityEnum) {
        List<CfgActivityEntity> entities = ActivityTool.getActivitiesByType(ActivityEnum);
        for (CfgActivityEntity entity : entities) {
            if (entity.getId() == id) {
                return JSONUtil.fromJsonArray(entity.getAwards(), Award.class);
            }
        }
        return new ArrayList<>();
    }
}
