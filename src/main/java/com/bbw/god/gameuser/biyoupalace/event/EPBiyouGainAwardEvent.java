package com.bbw.god.gameuser.biyoupalace.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年10月24日 下午3:54:53 
* 类说明 
*/
public class EPBiyouGainAwardEvent extends ApplicationEvent implements IEventParam{
	
	private static final long serialVersionUID = 1L;

	public EPBiyouGainAwardEvent(EPBiyouGainAward source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPBiyouGainAward getEP() {
		return (EPBiyouGainAward)getSource();
	}
}
