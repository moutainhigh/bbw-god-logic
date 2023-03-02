package com.bbw.god.gameuser.task.biggodplan.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合服折扣优惠刷新折扣事件基础参数
 *
 * @author: huanghb
 * @date: 2022/2/17 14:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCombinedServiceDiscount extends BaseEventParam {
    private Double discount;

    public EPCombinedServiceDiscount(Double discount, BaseEventParam bep) {
        this.discount = discount;
        setValues(bep);
    }
}
