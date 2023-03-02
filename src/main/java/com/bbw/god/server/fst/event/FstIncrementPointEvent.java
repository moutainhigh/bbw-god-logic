package com.bbw.god.server.fst.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:06:55 
* 类说明 封神台积分增长事件
*/
public class FstIncrementPointEvent extends ApplicationEvent implements IEventParam{

	private static final long serialVersionUID = 1L;

	public FstIncrementPointEvent(FstIncrementPoint  param) {
		super(param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public FstIncrementPoint getEP() {
		return (FstIncrementPoint)getSource();
	}

}
