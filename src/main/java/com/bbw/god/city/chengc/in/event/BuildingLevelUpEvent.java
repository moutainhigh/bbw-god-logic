package com.bbw.god.city.chengc.in.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

/**
 * 建筑升级事件
 * 
 * @author suhq
 * @date 2019-05-23 17:57:47
 */
public class BuildingLevelUpEvent extends ApplicationEvent {

	public BuildingLevelUpEvent(EventParam<EPBuildingLevelUp> source) {
		super(source);
	}

}
