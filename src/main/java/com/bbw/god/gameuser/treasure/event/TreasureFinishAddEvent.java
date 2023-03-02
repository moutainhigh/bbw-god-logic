package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 道具完成发放事件
 *
 * @author fzj
 * @date 2022/4/7 9:35
 */
public class TreasureFinishAddEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public TreasureFinishAddEvent(EPTreasureFinishAdd source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPTreasureFinishAdd getEP() {
        return (EPTreasureFinishAdd) getSource();
    }
}
