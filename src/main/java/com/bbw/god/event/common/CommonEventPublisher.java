package com.bbw.god.event.common;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.notify.rednotice.ModuleEnum;

/**
 * 通用事件推送器
 *
 * @author suhq
 * @date 2020-02-12 21:13:58
 */
public class CommonEventPublisher {


    public static void pubAccomplishEvent(long uid, ModuleEnum module, int type, int id) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPAccomplish ep = new EPAccomplish(bep, module, type, id);
        SpringContextUtil.publishEvent(new AccomplishEvent(ep));
    }
}
