package com.bbw.god.gameuser.res.dice;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 体力增加事件
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:26
 *
 */
public class DiceAddEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public DiceAddEvent(EPDiceAdd eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDiceAdd getEP() {
		return (EPDiceAdd) getSource();
	}

}
