package com.bbw.god.gameuser.chamberofcommerce.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.IEventParam;

/**
 * 商会任务完成事件
* @author lwb  
* @date 2019年6月24日  
* @version 1.0
 */
public class CocUpLvEvent extends ApplicationEvent implements IEventParam{
	private static final long serialVersionUID = 1L;

	public CocUpLvEvent(EPCocUpLv source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCocUpLv getEP() {
		return (EPCocUpLv)getSource();
	}

}
