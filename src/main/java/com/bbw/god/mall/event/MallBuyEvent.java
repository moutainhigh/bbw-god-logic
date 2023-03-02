package com.bbw.god.mall.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 商城购买事件
 *
 * @author fzj
 * @date 2021/8/19 9:40
 */
public class MallBuyEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 8851933144390638086L;

    public MallBuyEvent(EPMallBuy source) {
        super(source);
    }

    @Override
    public EPMallBuy getEP() {
        return (EPMallBuy) getSource();
    }
}
