package com.bbw.god.server.guild.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 行会宝箱开启事件
 * @date 2020/2/24 14:37
 */
public class GuildOpenBoxEvent extends ApplicationEvent implements IEventParam {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public GuildOpenBoxEvent(EPGuildOpenBox source) {
		super(source);
	}

	@Override
	public EPGuildOpenBox getEP() {
		return (EPGuildOpenBox) getSource();
	}
}
