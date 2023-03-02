package com.bbw.god.gameuser.task.daily.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;


/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午11:13:00 
* 类说明 每日任务积分获取事件
*/
public class DailyTaskAddPointEvent extends ApplicationEvent implements IEventParam{
	private static final long serialVersionUID = 1L;

	public DailyTaskAddPointEvent(DailyTaskAddPoint dta) {
		super(dta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DailyTaskAddPoint getEP() {
		return (DailyTaskAddPoint)getSource();
	}
	
}
