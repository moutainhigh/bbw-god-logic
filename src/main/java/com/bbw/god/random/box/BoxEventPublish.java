package com.bbw.god.random.box;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

/**
 * @author suchaobin
 * @description 箱子事件发布推送器
 * @date 2020/2/21 9:41
 */
public class BoxEventPublish {
	public static void pubOpenBoxEvent(int boxId, int score, Long guId, WayEnum way, RDCommon rd) {
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		SpringContextUtil.publishEvent(new OpenBoxEvent(new EPOpenBox(boxId, score, bep)));
	}
}
