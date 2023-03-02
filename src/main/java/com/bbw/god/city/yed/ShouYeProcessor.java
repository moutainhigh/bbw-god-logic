package com.bbw.god.city.yed;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardExpTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 仙人授业处理器
 * @date 2020/6/2 11:19
 **/
@Service
public class ShouYeProcessor extends BaseYeDEventProcessor {
	@Autowired
	private UserCardService userCardService;

	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.XRSY.getValue();
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
		List<UserCard> userCards = userCardService.getUserCards(uid);
		UserCard userCard = PowerRandom.getRandomFromList(userCards);
		CfgCardEntity cfgCard = CardTool.getCardById(userCard.getBaseId());
		int exp = CardExpTool.getNeededExpByLevel(cfgCard, userCard.getLevel() + 1) / 10;
		exp = exp / 1000 * 1000;
		int ableMaxExp = Math.max(1000, exp);
		ableMaxExp = Math.min(ableMaxExp, 20000);
		UserAdventure adventure = UserAdventure.instanceShouYe(uid, AdventureType.XRSY.getValue(),
				userCard.getBaseId(), ableMaxExp);
		gameUserService.addItem(uid, adventure);
		int waitMinute = (ableMaxExp - 1000) / 1000 * 5 + 10;
		Date date = DateUtil.addMinutes(adventure.getGenerateTime(), waitMinute);
		long remainTime = date.getTime() - System.currentTimeMillis();
		rdArriveYeD.setRemainTime(remainTime);
		rdArriveYeD.setExp(ableMaxExp);
		rdArriveYeD.setCardId(userCard.getBaseId());
		BaseEventParam bep = new BaseEventParam(uid, WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.XRSY));
		m2cService.sendDynamicMenu(uid, DynamicMenuEnum.ADVENTURE, 1);
	}
}
