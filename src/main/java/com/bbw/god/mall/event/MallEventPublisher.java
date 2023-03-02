package com.bbw.god.mall.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;

/**
 * 商店事件推送器
 *
 * @author suhq
 * @date 2019年3月1日 下午1:51:37
 */
public class MallEventPublisher {

    /**
     * 发布助力礼包激活事件
     *
     * @param bep
     */
    public static void pubZLActiveEvent(BaseEventParam bep) {
        SpringContextUtil.publishEvent(new ZLActiveEvent(new EventParam<>(bep)));
    }

    /**
     * 商城购买事件
     *
     * @param goodsId
     * @param mallType
     * @param num
     * @param bep
     */
    public static void pubMallbuySendEvent(Integer goodsId, Integer mallType, Integer num, BaseEventParam bep) {
        SpringContextUtil.publishEvent(new MallBuyEvent(new EPMallBuy(goodsId, mallType, num, bep)));
    }
}
