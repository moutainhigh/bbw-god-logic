package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 删除法宝记录事件
 * @date 2020/11/3 10:41
 **/
public class TreasureRecordDelEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public TreasureRecordDelEvent(EPTreasureRecordDel source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPTreasureRecordDel getEP() {
        return (EPTreasureRecordDel) getSource();
    }
}
