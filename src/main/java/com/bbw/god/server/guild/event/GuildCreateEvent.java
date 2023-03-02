package com.bbw.god.server.guild.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 行会创建事件
 * @author lwb
 *
 */
public class GuildCreateEvent extends ApplicationEvent implements IEventParam {
    public GuildCreateEvent(EPGuildCreate source) {
        super(source);
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public EPGuildCreate getEP() {
        return (EPGuildCreate) getSource();
    }
}
