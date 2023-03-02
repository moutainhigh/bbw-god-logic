package com.bbw.god.gameuser.businessgang.digfortreasure.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 挖宝事件发布器
 *
 * @author fzj
 * @date 2022/1/29 13:46
 */
public class DigTreasureEventPublisher {

    public static void pubDigTreasureEvent(long uid, int pos) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPDigTreasure ep = new EPDigTreasure(pos, bep);
        SpringContextUtil.publishEvent(new DigTreasureEvent(ep));
    }
}
