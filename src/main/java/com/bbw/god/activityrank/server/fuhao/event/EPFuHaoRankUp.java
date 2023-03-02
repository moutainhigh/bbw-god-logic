package com.bbw.god.activityrank.server.fuhao.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.Map;

/**
 * @author suchaobin
 * @description 富豪榜排名上升事件参数
 * @date 2020/2/5 9:50
 */
@Data
public class EPFuHaoRankUp extends BaseEventParam {
	// key是uid, value是排名变化前的排名
	private Map<Long, Integer> oldRankMap;

	public static EPFuHaoRankUp instance(Long guId, WayEnum way, Map<Long, Integer> oldRankMap) {
		EPFuHaoRankUp ev = new EPFuHaoRankUp();
		ev.setGuId(guId);
		ev.setWay(way);
		ev.setOldRankMap(oldRankMap);
		return ev;
	}

	public static EPFuHaoRankUp instance(Long guId, WayEnum way, RDCommon rd) {
		EPFuHaoRankUp ev = new EPFuHaoRankUp();
		ev.setGuId(guId);
		ev.setWay(way);
		ev.setRd(rd);
		return ev;
	}
}
