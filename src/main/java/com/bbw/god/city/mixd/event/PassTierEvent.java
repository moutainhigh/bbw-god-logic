package com.bbw.god.city.mixd.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/** 梦魇迷仙洞通过一层事件
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/
public class PassTierEvent extends ApplicationEvent implements IEventParam{

	private static final long serialVersionUID = 1L;

	public PassTierEvent(EPPassTier source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPPassTier getEP() {
		return (EPPassTier)getSource();
	}
}
