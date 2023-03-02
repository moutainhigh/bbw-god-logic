package com.bbw.god.game.zxz.event;

import com.bbw.god.event.IEventParam;
import com.bbw.god.gameuser.card.equipment.event.EPZxzClearanceScore;
import org.springframework.context.ApplicationEvent;

/**
 * 诛仙阵扫荡分事件
 *
 * @author: huanghb
 * @date: 2022/9/24 11:17
 */
public class ZxzClearanceScoreEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1;

	public ZxzClearanceScoreEvent(EPZxzClearanceScore source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPZxzClearanceScore getEP() {
		return (EPZxzClearanceScore) getSource();
	}
}
