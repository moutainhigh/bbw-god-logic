package com.bbw.god.gameuser.chamberofcommerce.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 商会任务完成事件
 *
 * @author lwb
 * @version 1.0
 * @date 2019年6月24日
 */
public class CocTaskFinishedEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;
	public CocTaskFinishedEvent(EPTaskFinished source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPTaskFinished getEP() {
		return (EPTaskFinished) getSource();
	}
}
