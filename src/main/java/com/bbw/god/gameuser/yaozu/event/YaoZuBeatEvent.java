package com.bbw.god.gameuser.yaozu.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 击败妖族事件
 *
 * @author fzj
 * @date 2021/9/8 15:13
 */
public class YaoZuBeatEvent extends ApplicationEvent implements IEventParam {
    private static final long serialVersionUID = 1L;

    public YaoZuBeatEvent(EPYaoZu yaoZu) {
        super(yaoZu);
    }

    @Override
    public EPYaoZu getEP() {
        return (EPYaoZu) getSource();
    }
}
