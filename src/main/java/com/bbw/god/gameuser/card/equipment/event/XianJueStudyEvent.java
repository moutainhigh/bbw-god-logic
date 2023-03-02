package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 仙诀研习满级事件
 *
 * @author: huanghb
 * @date: 2022/9/24 11:17
 */
public class XianJueStudyEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1;

	public XianJueStudyEvent(EPXianJueStudy source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPXianJueStudy getEP() {
		return (EPXianJueStudy) getSource();
	}
}
