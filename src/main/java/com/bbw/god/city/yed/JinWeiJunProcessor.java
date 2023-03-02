package com.bbw.god.city.yed;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 禁卫军处理器
 * @date 2020/6/2 17:18
 **/
@Service
public class JinWeiJunProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.JIN_WEI_JUN.getValue();
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
		long uid = gameUser.getId();
		if (gameUser.getLevel() < 24) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		RDYeDEventCache cache = TimeLimitCacheUtil.getYeDEventCache(uid);
		cache.addEvent(getMyId());
		TimeLimitCacheUtil.setYeDEventCache(uid, cache);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.JIN_WEI_JUN));
	}
}
