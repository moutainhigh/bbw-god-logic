package com.bbw.god.road;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * value - Integer Road的id
 * 
 * @author suhq
 * @date 2018年10月9日 下午2:50:23
 */
public class RoadEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public RoadEvent(EPRoad ep) {
		super(ep);
	}

	@Override
	public EPRoad getEP() {
		return (EPRoad)getSource();
	}
}
