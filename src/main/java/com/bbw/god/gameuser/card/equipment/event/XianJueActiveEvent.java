package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 仙诀激活达成事件参数
 *
 * @author suhq
 * @date 2021/7/2 上午9:18
 **/
public class XianJueActiveEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1L;

    public XianJueActiveEvent(EPXianJueActive source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPXianJueActive getEP() {
        return (EPXianJueActive) getSource();
    }
}
