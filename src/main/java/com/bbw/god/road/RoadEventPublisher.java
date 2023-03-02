package com.bbw.god.road;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDAdvance;

/**
 * 格子事件触发器
 * 
 * @author suhq
 * @date 2018年11月24日 下午9:33:16
 */
public class RoadEventPublisher {
	public static void publishRoadEvent(Long guId, int roadId,WayEnum way, RDAdvance rd) {
		BaseEventParam bep = new BaseEventParam(guId,way,rd);
		EPRoad ep = new EPRoad(bep,roadId);
		SpringContextUtil.publishEvent(new RoadEvent(ep));
	}

}
