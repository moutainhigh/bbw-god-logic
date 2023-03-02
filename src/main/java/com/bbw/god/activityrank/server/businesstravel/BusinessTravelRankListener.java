package com.bbw.god.activityrank.server.businesstravel;

import java.util.Arrays;
import java.util.List;

import com.bbw.god.city.mixd.EPOutMxd;
import com.bbw.god.city.nvwm.EPNvWMDonate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.city.event.CityArriveEvent;
import com.bbw.god.city.lut.LtTributeEvent;
import com.bbw.god.city.miaoy.EPMiaoYDrawEnd;
import com.bbw.god.city.miaoy.MiaoYDrawEndEvent;
import com.bbw.god.city.mixd.OutMxdEvent;
import com.bbw.god.city.nvwm.NwmDonateEvent;
import com.bbw.god.city.taiyf.TyfFillEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;

/**
 * 商周游历榜
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Async
@Component
public class BusinessTravelRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.TRAVEL_RANK;
	private List<CityTypeEnum> arriveExclude = Arrays.asList(CityTypeEnum.NWM, CityTypeEnum.MY, CityTypeEnum.TYF, CityTypeEnum.MXD, CityTypeEnum.LT, CityTypeEnum.CC1, CityTypeEnum.CC2, CityTypeEnum.CC3, CityTypeEnum.CC4, CityTypeEnum.CC5);

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	@Order(1000)
	public void cocTaskFinish(CocTaskFinishedEvent event) {
		EPTaskFinished ep = event.getEP();
		Long uid = ep.getGuId();
		int point = 6 * ep.getLevel();
		activityRankService.incrementRankValue(uid, point, rankType);
	}

	@EventListener
	@Order(1000)
	public void cocTaskFinish(GuildTaskFinishedEvent event) {
		EPGuildTaskFinished ep = event.getEP();
		activityRankService.incrementRankValue(ep.getGuId(), 4, rankType);
	}

	@EventListener
	@Order(1000)
	public void arriveCity(CityArriveEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		CfgRoadEntity road = RoadTool.getRoadById(ep.getValue());
		CfgCityEntity city = road.getCity();
		CityTypeEnum cityType = CityTypeEnum.fromValue(city.getType());
		if (arriveExclude.contains(cityType)) {
			return;
		}
		activityRankService.incrementRankValue(ep.getGuId(), 1, rankType);
	}

	@EventListener
	@Order(1000)
	public void miaoYDrawEnd(MiaoYDrawEndEvent event) {
		EventParam<EPMiaoYDrawEnd> ep = (EventParam<EPMiaoYDrawEnd>) event.getSource();
		activityRankService.incrementRankValue(ep.getGuId(), 3, rankType);
	}

	@EventListener
	@Order(1000)
	public void outMxd(OutMxdEvent event) {
		EPOutMxd ep = event.getEP();
		activityRankService.incrementRankValue(ep.getGuId(), 3, rankType);
	}

	@EventListener
	@Order(1000)
	public void nwmDonate(NwmDonateEvent event) {
		EPNvWMDonate ep = event.getEP();
		activityRankService.incrementRankValue(ep.getGuId(), 3, rankType);
	}

	@EventListener
	@Order(1000)
	public void ltTribute(LtTributeEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		activityRankService.incrementRankValue(ep.getGuId(), 3, rankType);
	}

	@EventListener
	@Order(1000)
	public void tyfFill(TyfFillEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		activityRankService.incrementRankValue(ep.getGuId(), 3, rankType);
	}
}
