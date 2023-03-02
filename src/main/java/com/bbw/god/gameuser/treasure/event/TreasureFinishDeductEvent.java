package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 道具完成扣除事件
 *
 * @author fzj
 * @date 2022/4/7 9:42
 */
public class TreasureFinishDeductEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public TreasureFinishDeductEvent(EPTreasureFinishDeduct source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPTreasureFinishDeduct getEP() {
        return (EPTreasureFinishDeduct) getSource();
    }
}
