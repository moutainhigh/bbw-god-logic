package com.bbw.god.game.wanxianzhen.event;

import com.bbw.common.SpringContextUtil;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
public class WanXianEventPublisher {
    /**
     * 进入64强
     *
     */
    public static void pubEPWanXianInto64(long uid) {
        EPWanXianInto64 ep=new EPWanXianInto64();
        ep.setGuId(uid);
        SpringContextUtil.publishEvent(new WanXianInto64Event(ep));
    }
}
