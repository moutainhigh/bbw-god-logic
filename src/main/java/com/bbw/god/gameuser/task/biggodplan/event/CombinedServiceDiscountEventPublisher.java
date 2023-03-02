package com.bbw.god.gameuser.task.biggodplan.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 合服折扣优惠刷新折扣事件发布器
 *
 * @author: huanghb
 * @date: 2022/2/17 14:19
 */
public class CombinedServiceDiscountEventPublisher {
    public static void combinedServiceDiscountEvent(Double point, BaseEventParam bep) {
        SpringContextUtil.publishEvent(new CombinedServiceDiscountEvent(new EPCombinedServiceDiscount(point, bep)));
    }
}
