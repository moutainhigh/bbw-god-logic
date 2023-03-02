package com.bbw.god.city.yed;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.god.processor.AbstractGodProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 天师借法处理器
 * @date 2020/6/2 16:56
 **/
@Service
public class TianSJFProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.TSJF.getValue();
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
		GodEnum[] godEnums = GodEnum.values();
		List<GodEnum> godEnumList = Arrays.stream(godEnums).filter(godEnum ->
				GodEnum.BBX != godEnum).collect(Collectors.toList());
		GodEnum godEnum = PowerRandom.getRandomFromList(godEnumList);
		// 构造一个虚拟的神仙，避免地图上对应的神仙消失
		ServerGod serverGod = godService.getUnrealServerGod(gameUser.getServerId(), godEnum.getValue());
		long uid = gameUser.getId();
		UserGod userGod = UserGod.instance(uid, serverGod);
		godService.attachGod(gameUser, userGod);
		AbstractGodProcessor godProcessor = godProcessorFactory.create(uid, userGod.getBaseId());
		RDArriveYeD.RDGodReward godReward = new RDArriveYeD.RDGodReward();
		godProcessor.processor(gameUser, userGod, godReward);// 业务处理
		rdArriveYeD.setGodId(serverGod.getGodId());
		rdArriveYeD.setGodReward(godReward);
		long godRemainTime = userGod.getAttachEndTime().getTime() - userGod.getAttachTime().getTime();
		rdArriveYeD.setGodRemainTime(godRemainTime);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromEnum(YdEventEnum.TSJF));
	}
}
