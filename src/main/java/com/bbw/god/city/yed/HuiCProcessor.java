package com.bbw.god.city.yed;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.ChengCInProcessor;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.*;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.RDAdvance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 回城处理器
 * @date 2020/6/2 17:35
 **/
@Service
@Slf4j
public class HuiCProcessor extends BaseYeDEventProcessor {
	@Autowired
	private UserCityService userCityService;
	@Autowired
	protected ChengCInProcessor chengCInProcessor;

	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.HC.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		if (gameUser.getLevel() <= 10) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		long uid = gameUser.getId();
		CfgCityEntity city =null;
		if (gameUser.getStatus().intoNightmareWord()) {
			List<UserNightmareCity> nightmareCities = userCityService.getUserOwnNightmareCities(uid);
			if (ListUtil.isEmpty(nightmareCities)) {
				rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
				return;
			}
			UserNightmareCity userCity = PowerRandom.getRandomFromList(nightmareCities);
			city = CityTool.getCityById(userCity.getBaseId());
		} else if (gameUser.getStatus().ifInTransmigrateWord()) {
			ChengC randomChengC = PowerRandom.getRandomFromList(CityTool.getChengCs());
			city = CityTool.getCityById(randomChengC.getId());
		} else {
			List<UserCity> userCities = userCityService.getUserOwnCities(uid);
			if (ListUtil.isEmpty(userCities)) {
				rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
				return;
			}
			UserCity userCity = PowerRandom.getRandomFromList(userCities);
			city = CityTool.getCityById(userCity.getBaseId());
		}
		CfgRoadEntity road = RoadTool.getRoadById(city.getAddress1());
		int direction = road.getDirectionByRandom();
		rdArriveYeD.setPos(road.getId());
		rdArriveYeD.setDirection(direction);
		gameUser.moveTo(road.getId(), direction);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.HC));
		// 到达
		RDArriveChengC rdArriveChengC = chengCProcessor.arriveProcessor(gameUser, city, rd);
		rdArriveChengC.setArriveCityId(city.getId());
		rdArriveYeD.setRdCityInfo(rdArriveChengC);
		// 缓存到达位置的临时数据，每到一个位置该数据将被更新
		TimeLimitCacheUtil.setArriveCache(uid, rdArriveChengC);
	}
}
