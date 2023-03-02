package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author lzc
 * @description 主角装备点满星图事件（武器、衣服、戒指、项链）
 * @date 2021/4/14 10:53
 */
public class LeaderEquipmentQualityFinishEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1;

	public LeaderEquipmentQualityFinishEvent(EPLeaderEquipmentQualityFinish source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPLeaderEquipmentQualityFinish getEP() {
		return (EPLeaderEquipmentQualityFinish) getSource();
	}
}
