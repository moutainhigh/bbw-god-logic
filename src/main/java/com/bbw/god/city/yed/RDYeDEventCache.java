package com.bbw.god.city.yed;

import com.bbw.god.game.config.city.YdEventEnum;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author suchaobin
 * @description 野地事件缓存
 * @date 2020/6/2 16:39
 **/
@Data
public class RDYeDEventCache {
	private Set<Integer> eventIds = new HashSet<>();

	public void addEvent(int eventId) {
		if (eventId == YdEventEnum.ZJZF.getValue() && eventIds.contains(YdEventEnum.CBNX.getValue())) {
			eventIds.remove(YdEventEnum.CBNX.getValue());
		}
		if (eventId == YdEventEnum.CBNX.getValue() && eventIds.contains(YdEventEnum.ZJZF.getValue())) {
			eventIds.remove(YdEventEnum.ZJZF.getValue());
		}
		this.eventIds.add(eventId);
	}
}
