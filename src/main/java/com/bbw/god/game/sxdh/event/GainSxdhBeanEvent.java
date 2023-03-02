package com.bbw.god.game.sxdh.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:31:44 
* 类说明  神仙大会仙豆领取事件
*/
public class GainSxdhBeanEvent extends ApplicationEvent implements IEventParam{

	private static final long serialVersionUID = 1L;

	public GainSxdhBeanEvent(GainSxdhBean source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public GainSxdhBean getEP() {
		return (GainSxdhBean)getSource();
	}

}
