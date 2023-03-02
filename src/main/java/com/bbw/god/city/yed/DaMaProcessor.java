package com.bbw.god.city.yed;

import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 大妈事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class DaMaProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.DA_MA.getValue();
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
		if (monthLoginLogic.isExistEvent(gameUser.getId(),MonthLoginEnum.GOOD_DM)){
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		long uid = gameUser.getId();
		List<UserSpecial> userSpecials = userSpecialService.getRandomEventSpecials(uid);
		if (userSpecials.size() == 0) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		int addedCopper = 0;
		int addedWeekCopper = 0;
		List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();
		for (UserSpecial us : userSpecials) {
			CfgSpecialEntity specialEntity = SpecialTool.getSpecialById(us.getBaseId());
			int boughtPrice = specialEntity.getPrice() * us.getDiscount() / 10;
			int price = specialEntity.getPrice();
			addedCopper += price;
			addedWeekCopper += (price - boughtPrice);
			EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(us.getId(), us.getBaseId(),
					boughtPrice, price);
			specialInfoList.add(info);
		}
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(bep, gameUser.getLocation().getPosition(),
				specialInfoList);
		SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
		EPCopperAdd copperInfo = new EPCopperAdd(bep, addedCopper, addedWeekCopper);
		ResEventPublisher.pubCopperAddEvent(copperInfo);
		// 广播
		List<Integer> specialIds =
				specialInfoList.stream().map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
		EPYeDTrigger epYeDTrigger = EPYeDTrigger.fromLoss(YdEventEnum.DA_MA, specialIds);
		CityEventPublisher.pubYeDTrigger(bep, epYeDTrigger);
	}
}
