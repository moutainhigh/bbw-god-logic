package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 神仙大会刷新卡牌
 *
 * @author suhq
 * @date 2020-04-28 23:18
 **/
public class SxdhCardRefreshEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1L;

    public SxdhCardRefreshEvent(EPSxdhCardRefresh source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPSxdhCardRefresh getEP() {
        return (EPSxdhCardRefresh) getSource();
    }

}
