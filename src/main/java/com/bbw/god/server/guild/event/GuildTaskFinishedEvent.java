package com.bbw.god.server.guild.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 行会任务
 * 
 * @author lwb
 * @date 2019年6月24日
 * @version 1.0
 */
public class GuildTaskFinishedEvent extends ApplicationEvent implements IEventParam{
	
	public GuildTaskFinishedEvent(EPGuildTaskFinished source) {
		super(source);
	}
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public EPGuildTaskFinished getEP() {
		return (EPGuildTaskFinished)getSource();
	}
	

}
