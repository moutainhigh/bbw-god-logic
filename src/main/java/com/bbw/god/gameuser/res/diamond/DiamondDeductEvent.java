package com.bbw.god.gameuser.res.diamond;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 钻石扣除事件
 *
 * @author: huanghb
 * @date: 2022/6/15 17:14
 */
public class DiamondDeductEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public DiamondDeductEvent(EPDiamondDeduct eventParam) {
		super(eventParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDiamondDeduct getEP() {
		return (EPDiamondDeduct) getSource();
	}

}
