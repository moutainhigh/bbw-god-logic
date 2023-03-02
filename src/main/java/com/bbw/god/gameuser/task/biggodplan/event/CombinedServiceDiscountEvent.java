package com.bbw.god.gameuser.task.biggodplan.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 合服折扣优惠刷新折扣事件
 *
 * @author: huanghb
 * @date: 2022/2/17 14:19
 */
public class CombinedServiceDiscountEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1092528418694657566L;

    public CombinedServiceDiscountEvent(EPCombinedServiceDiscount source) {
        super(source);
    }

    /**
     * 获取事件参数
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public EPCombinedServiceDiscount getEP() {
        return (EPCombinedServiceDiscount) getSource();
    }
}
