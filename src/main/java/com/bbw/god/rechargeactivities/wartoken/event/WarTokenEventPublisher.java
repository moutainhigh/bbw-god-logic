package com.bbw.god.rechargeactivities.wartoken.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-03
 */
public class WarTokenEventPublisher {
    /**
     * 增加经验
     * @param uid
     * @param exp
     */
    public static void pubAddExpEvent(long uid, int exp,boolean addWeekExp) {
        if (exp<=0){
            return;
        }
        BaseEventParam bep = new BaseEventParam(uid);
        EPWarTokenAddExp warTokenAddExp = EPWarTokenAddExp.getInstance(exp,addWeekExp,bep);
        SpringContextUtil.publishEvent(new WarTokenAddExpEvent(warTokenAddExp));
    }

    public static void pubActiveEvent(long uid) {
        BaseEventParam bep = new BaseEventParam(uid);
        SpringContextUtil.publishEvent(new WarTokenActiveEvent(bep));
    }
}
