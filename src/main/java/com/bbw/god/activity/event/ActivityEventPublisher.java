package com.bbw.god.activity.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

/**
 * 活动事件发送器
 *
 * @author suhq
 * @date 2019年3月6日 下午3:01:35
 */
public class ActivityEventPublisher {

    public static void pubActivityAccomplishEvent(long uid, CfgActivityEntity ca, RDCommon rd) {
        BaseEventParam bep = new BaseEventParam(uid, WayEnum.NONE, rd);
        SpringContextUtil.publishEvent(new ActivityAccomplishEvent(new EPActivityAccomplish(bep, ca)));
    }

}
