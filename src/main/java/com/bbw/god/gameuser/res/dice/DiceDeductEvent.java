package com.bbw.god.gameuser.res.dice;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * 体力扣除事件
 * 
 * @author suhq
 * @date 创建时间：2018年9月13日 下午2:23:31
 *
 */
public class DiceDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public DiceDeductEvent(EPDiceDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDiceDeduct getEP() {
		return (EPDiceDeduct) getSource();
	}

}
