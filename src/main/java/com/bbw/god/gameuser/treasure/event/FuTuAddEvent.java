package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 符图添加事件
 *
 * @author fzj
 * @date 2022/1/4 16:59
 */
public class FuTuAddEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public FuTuAddEvent(EPFuTuAdd source) {
        super(source);
    }


    @Override
    public EPFuTuAdd getEP() {
        return (EPFuTuAdd) getSource();
    }
}
