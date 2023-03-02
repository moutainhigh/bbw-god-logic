package com.bbw.god.server.guild.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月16日 下午4:55:47 
* 类说明 
*/
public class GuildAddExpEvent extends ApplicationEvent implements IEventParam {
	public GuildAddExpEvent(EPAddGuildExp source) {
		super(source);
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public EPAddGuildExp getEP() {
		return (EPAddGuildExp) getSource();
	}
}
