package com.bbw.god.city.yed;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.fight.processor.YeDFightProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 小偷事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class XiaoTouProcessor extends ExtraYeDEventProcessor implements YeDEventExtraOperation {
	@Autowired
	private YeDFightProcessor yeDFightProcessor;
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.XIAO_TOU.getValue();
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
		//卦象buff
		if (hasHexagramBuff(gameUser.getId())){
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		Optional<UserGod> userGod = godService.getAttachGod(gameUser);
		// 送子观音附体
		if (userGod.isPresent() && userGod.get().getBaseId() == GodEnum.SZGY.getValue()) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		int num = 1;
		if (gameUser.getLevel() > 20) {
			num = PowerRandom.getRandomBetween(1, 2);
		} else if (gameUser.getLevel() > 60) {
			num = 2;
		}
		List<UserSpecial> userSpecials = userSpecialService.getRandomEventSpecials(gameUser.getId(), num);
		if (userSpecials.size() == 0) {
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		List<EPSpecialDeduct.SpecialInfo> specialInfoList = new ArrayList<>();
		for (UserSpecial userSpecial : userSpecials) {
			CfgSpecialEntity special = SpecialTool.getSpecialById(userSpecial.getBaseId());
			EPSpecialDeduct.SpecialInfo info = EPSpecialDeduct.SpecialInfo.getInstance(userSpecial.getId(),
					special.getId(), special.getBuyPrice(userSpecial.getDiscount()));
			specialInfoList.add(info);
		}
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		List<Integer> specialIds = specialInfoList.stream()
				.map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
		rdArriveYeD.setReduceSpcialIds(specialIds);
		TimeLimitCacheUtil.setArriveCache(gameUser.getId(), rdArriveYeD);
		EPSpecialDeduct epSpecialDeduct = EPSpecialDeduct.instance(bep, gameUser.getLocation().getPosition(),
				specialInfoList);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromLoss(YdEventEnum.XIAO_TOU, specialIds));
		SpecialEventPublisher.pubSpecialDeductEvent(epSpecialDeduct);
		rdArriveYeD.setReduceSpecial(userSpecials.stream().map(UserSpecial::getId).collect(Collectors.toList()));
	}

	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 */
	@Override
	public void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd) {
		List<UserCity> ownCities = userCityService.getUserOwnCities(gu.getId());
		int cityLv=1;
		int cityId=0;
		if (ListUtil.isEmpty(ownCities)){
			List<Integer> ids = CityTool.getAllCityIdByLevel(1);
			cityId=PowerRandom.getRandomFromList(ids);
		}else {
			UserCity random = PowerRandom.getRandomFromList(ownCities);
			cityLv=random.gainCity().getLevel();
			cityId=random.getBaseId();
		}
		RDFightsInfo fightsInfo = yeDFightProcessor.buildRDFightsInfo(gu.getId(),cityLv,cityId,YdEventEnum.XIAO_TOU);
		fightsInfo.setNickname("小偷");
		fightsInfo.setHead(TreasureEnum.HEAD_XT.getValue());
		fightsInfo.setYeDEventType(getMyId());
		// 初始战斗为未结算
		TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
		// 设置更新缓存
		arriveYeD.setFightsInfo(fightsInfo);
		arriveYeD.setFightType(FightTypeEnum.YED_EVENT.getValue());
		TimeLimitCacheUtil.setArriveCache(gu.getId(), arriveYeD);
	}
}
