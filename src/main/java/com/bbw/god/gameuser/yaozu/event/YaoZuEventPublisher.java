package com.bbw.god.gameuser.yaozu.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 妖族来袭事件发布器
 *
 * @author fzj
 * @date 2021/9/7 8:40
 */
public class YaoZuEventPublisher {
    /**
     * 击败妖族事件
     * @param uid
     * @param yaoZuId 妖族id
     */
    public static void pubYaoZuBeatEvent(long uid, int yaoZuId) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPYaoZu ep = new EPYaoZu(yaoZuId,bep);
        SpringContextUtil.publishEvent(new YaoZuBeatEvent(ep));
    }
}
