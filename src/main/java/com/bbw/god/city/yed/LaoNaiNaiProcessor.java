package com.bbw.god.city.yed;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.special.SpecialTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 老奶奶事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class LaoNaiNaiProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.LAO_NAI_NAI.getValue();
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
		int num = 1;
		if (gameUser.getLevel() > 20) {
			num = PowerRandom.getRandomBetween(1, 2);
		} else if (gameUser.getLevel() > 60) {
			num = 2;
		}
		if (monthLoginLogic.isExistEvent(gameUser.getId(),MonthLoginEnum.GOOD_LNN)){
			num*=2;
		}
		List<EVSpecialAdd> specialAdds = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			int random = PowerRandom.getRandomBySeed(2);
			SpecialTypeEnum type = random == 2 ? SpecialTypeEnum.HIGH : SpecialTypeEnum.NORMAL;
			specialAdds.add(new EVSpecialAdd(SpecialTool.getRandomSpecial(type).getId(), 0));
		}
		SpecialEventPublisher.pubSpecialAddEvent(gameUser.getId(), specialAdds, WayEnum.YD, rd);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		List<Integer> specialIds = specialAdds.stream().map(EVSpecialAdd::getSpecialId).collect(Collectors.toList());
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromIncome(YdEventEnum.LAO_NAI_NAI, specialIds));
	}
}
