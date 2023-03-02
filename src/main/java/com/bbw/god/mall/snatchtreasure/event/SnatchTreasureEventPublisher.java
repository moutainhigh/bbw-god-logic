package com.bbw.god.mall.snatchtreasure.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

/**
 * @author suchaobin
 * @description 夺宝事件发布器
 * @date 2020/6/30 14:54
 **/
public class SnatchTreasureEventPublisher {
	public static void pubDrawEvent(long uid, WayEnum way, RDCommon rd, Integer drawTimes, Integer consumeTreasureId) {
		BaseEventParam bep = new BaseEventParam(uid, way, rd);
		EPSnatchTreasureDraw ep = new EPSnatchTreasureDraw(drawTimes, consumeTreasureId, bep);
		SpringContextUtil.publishEvent(new SnatchTreasureDrawEvent(ep));
	}
}
