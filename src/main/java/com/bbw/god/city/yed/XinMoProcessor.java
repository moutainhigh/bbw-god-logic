package com.bbw.god.city.yed;

import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 心魔处理器
 * @date 2020/6/2 17:06
 **/
@Service
public class XinMoProcessor extends ExtraYeDEventProcessor implements YeDEventExtraOperation {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.XIN_MO.getValue();
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
		if (gameUser.getLevel() < 20) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.XIN_MO));
	}

	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 *
	 * @param gu
	 * @param arriveYeD
	 */
	@Override
	public void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd) {
		long uid = gu.getId();
		List<UserCard> fightingCards = userCardService.getFightingCards(uid);
		RDFightsInfo fightsInfo = RDFightsInfo.instance(gu, fightingCards);
		fightsInfo.setNickname(fightsInfo.getNickname() + "(心魔)");
		fightsInfo.setYeDEventType(getMyId());
		fightsInfo.setCardFromUid(gu.getId() * -1);
		// 初始战斗为未结算
		TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
		// 设置更新缓存
		arriveYeD.setFightsInfo(fightsInfo);
		arriveYeD.setFightType(FightTypeEnum.YED_EVENT.getValue());
		TimeLimitCacheUtil.setArriveCache(gu.getId(), arriveYeD);
	}
}
