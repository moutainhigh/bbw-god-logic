package com.bbw.god.gameuser.statistic.resource.city;

import java.util.List;

import com.bbw.exception.ErrorLevel;
import com.bbw.mc.mail.MailAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.common.DateUtil;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;

import lombok.extern.slf4j.Slf4j;

/**
 * @author suchaobin
 * @description 城池统计监听类
 * @date 2020/4/15 14:12
 */
@Component
@Slf4j
@Async
public class CityResListener {
	@Autowired
	private CityResStatisticService cityResStatisticService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private MailAction mailAction;

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void addCity(UserCityAddEvent event) {
		try {
			EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
			// 非原世界的直接return
			if (ep.getValue().isNightmare()) {
				return;
			}
			EPCityAdd epCityAdd = ep.getValue();
			int cityId = epCityAdd.getCityId();
			CfgCityEntity city = CityTool.getCityById(cityId);
			cityResStatisticService.addCity(ep.getGuId(), city.getLevel(), city.getCountry());
			CityStatistic statistic = cityResStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
			StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			mailAction.notifyCoder(ErrorLevel.HIGH,"获得封地统计异常",e.getMessage());
		}
	}

	@Order(2)
	@EventListener
	@SuppressWarnings("unchecked")
	public void buildingLevelUp(BuildingLevelUpEvent event) {
		try {
			EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
			EPBuildingLevelUp epValue = ep.getValue();
			List<Integer> levelUpBuildings = epValue.getLevelUpBuildings();
			Integer cityId = epValue.getCityId();
			UserCity userCity = gameUserService.getMultiItems(ep.getGuId(), UserCity.class).stream().filter(city -> cityId.equals(city.getBaseId())).findFirst().orElse(null);
			if (null != userCity) {
				int minLevel = levelUpBuildings.stream().map(userCity::getBuildingLevel).min(Integer::compareTo).get();
				if (userCity.ifUpdate(minLevel)) {
					cityResStatisticService.addAllLevelCity(ep.getGuId(), minLevel);
					CityStatistic statistic = cityResStatisticService.fromRedis(ep.getGuId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
					StatisticEventPublisher.pubResourceStatisticEvent(ep.getGuId(), ep.getWay(), ep.getRd(), statistic);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
