package com.bbw.god.gameuser.biyoupalace.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 碧游宫领悟达成事件参数
 *
 * @author suhq
 * @date 2021/7/2 上午9:18
 **/
public class BiyouRealizedEvent extends ApplicationEvent implements IEventParam {

    private static final long serialVersionUID = 1L;

    public BiyouRealizedEvent(EPBiyouRealized source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EPBiyouRealized getEP() {
        return (EPBiyouRealized) getSource();
    }
}
