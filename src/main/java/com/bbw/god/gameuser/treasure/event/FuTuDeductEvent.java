package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 符图扣除事件
 *
 * @author fzj
 * @date 2022/1/4 17:30
 */
public class FuTuDeductEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public FuTuDeductEvent(EPFuTuDeduct source) {
        super(source);
    }


    @Override
    public EPFuTuDeduct getEP() {
        return (EPFuTuDeduct) getSource();
    }
}
