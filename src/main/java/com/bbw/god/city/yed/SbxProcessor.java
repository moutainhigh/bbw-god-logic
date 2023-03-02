package com.bbw.god.city.yed;

import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 四不像事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class SbxProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.SBX.getValue();
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
		int sbx = TreasureEnum.SBX.getValue();
		int addEffect = TreasureTool.getTreasureConfig().getTreasureEffectSBX();
		rdArriveYeD.setTreasureId(sbx);
		TreasureEventPublisher.pubTEffectAddEvent(gameUser.getId(), sbx, addEffect, WayEnum.YD, rd);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromIncome(YdEventEnum.SBX, addEffect));
	}
}
