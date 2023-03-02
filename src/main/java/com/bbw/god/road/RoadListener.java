package com.bbw.god.road;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RoadListener {
	@Autowired
	private GameUserService gameUserService;

	@EventListener
	public void checkRoad(RoadEvent event) {
		EPRoad ep = event.getEP();
		CfgRoadEntity road = RoadTool.getRoadById(ep.getRoadId());
		CfgCityEntity city = road.getCity();
		if (city.getType() == CityTypeEnum.JB.getValue()) {
			ResEventPublisher.pubCopperAddEvent(ep.getGuId(), 2000, WayEnum.JB, ep.getRd());
		}

	}

}
