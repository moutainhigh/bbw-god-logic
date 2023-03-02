package com.bbw.god.city.yed;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 税收事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class TaxProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.TAX.getValue();
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
		CfgCityEntity city = userCityService.getRandomUserOwnCity(gameUser.getId());
		if (city == null) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		int copyNum = PowerRandom.getRandomBetween(5, 8);
		int addedCopper = city.getBaseTax() * copyNum;
		rdArriveYeD.setCityName(city.getName());
		rdArriveYeD.setCopyNum(copyNum);
		ResEventPublisher.pubCopperAddEvent(gameUser.getId(), addedCopper, WayEnum.YD, rd);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromIncome(YdEventEnum.TAX, addedCopper));
	}
}
