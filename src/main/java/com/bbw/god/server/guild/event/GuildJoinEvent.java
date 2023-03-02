package com.bbw.god.server.guild.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 申请加入行会事件
 * @date 2019/12/19 14:46
 */
public class GuildJoinEvent extends ApplicationEvent implements IEventParam {
    public GuildJoinEvent(EPGuildJoin source) {
        super(source);
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public EPGuildJoin getEP() {
        return (EPGuildJoin) getSource();
    }
}
