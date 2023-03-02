package com.bbw.god.city.mixd;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.*;
import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.city.exp.CityExpService;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianService;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 迷仙洞
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class MiXDProcessor implements ICityArriveProcessor, ICityHandleProcessor, ICityExpProcessor {

	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.MXD);

	@Autowired
	private BoxService boxService;
	@Autowired
	private UserCityService userCityService;
	@Autowired
	private CityExpService cityExpService;

	@Autowired
	private NightmareMiXianService nightmareMiXianService;

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return cityTypes;
	}

	@Override
	public Class<RDArriveMiXD> getRDArriveClass() {
		return RDArriveMiXD.class;
	}

	@Override
	public RDArriveMiXD arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
		RDArriveMiXD rdArriveMiXD = new RDArriveMiXD();
		rdArriveMiXD.setIsExped(cityExpService.hasExped(gameUser.getId(), city));
		if (isActive(gameUser)) {
			UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(gameUser.getId());
			// 增加可挑战层次数
			nightmareMiXianService.incChallengeLayers(nightmareMiXian);
			// 可挑战层数大于0 才能进入 梦魇迷仙洞
			if (nightmareMiXian.getRemainChallengeLayers() > 0){
				rdArriveMiXD.setNightmareMxd(1);
				MiXDEventPublisher.pubIntoNightmareMxdEvent(gameUser.getId());
				return rdArriveMiXD;
			}
		}
		int[] invalidPos = {3, 21, 25, 48};
		List<Integer> poss = PowerRandom.getRandomInts(50, 4, invalidPos);
		rdArriveMiXD.setPoss(poss);
		rdArriveMiXD.setHandleStatus("1");
		return rdArriveMiXD;
	}

	@Override
	public RDCityInfo exp(GameUser gu, CfgCityEntity city) {
		RDArriveMiXD rdArriveMiXD = new RDArriveMiXD();
		rdArriveMiXD.setNightmareMxd(1);
		MiXDEventPublisher.pubIntoNightmareMxdEvent(gu.getId());
		return rdArriveMiXD;
	}

	@Override
	public RDCommon handleProcessor(GameUser gu, Object param) {
		RDCommon rd = new RDCommon();
		String[] roads = ((String) param).split(",");
		// 15步限制
		int length = roads.length;
		if (length > 15) {
			throw new ExceptionForClientTip("city.mxd.unvalid.step");
		}
		// 是否找到出口
		int lastRoad = Integer.valueOf(roads[length - 1]);
		if (lastRoad != 3 && lastRoad != 21 && lastRoad != 25 && lastRoad != 48) {
			throw new ExceptionForClientTip("city.mxd.not.findExit");
		}

		// 获得奖励格子
		List<Integer> awardPoss = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass()).getPoss();
		List<Integer> guAwardPos = new ArrayList<Integer>();
		for (int i = 0; i < roads.length; i++) {
			int road = Integer.valueOf(roads[i]);
			for (int j = 0; j < awardPoss.size(); j++) {
				if (road == awardPoss.get(j) && !guAwardPos.contains(j)) {
					guAwardPos.add(j);
				}
			}
		}
		// 发放奖励
		for (int i = 0; i < guAwardPos.size(); i++) {
			switch (guAwardPos.get(i)) {
				case 0:// 10元宝 改为10000铜钱
					ResEventPublisher.pubCopperAddEvent(gu.getId(), 10000, WayEnum.MXD, rd);
					break;
				case 1:// 20元宝
					ResEventPublisher.pubGoldAddEvent(gu.getId(), 20, WayEnum.MXD, rd);
					break;
				case 2:// 宝箱
					boxService.open(gu.getId(), TreasureEnum.BX.getValue(), WayEnum.MXD, rd);
					break;
				case 3:// 元素牌两张
					ResEventPublisher.pubEleAddEvent(gu.getId(), 2, WayEnum.MXD, rd);
					break;
				default:
					break;
			}
		}
		CityEventPublisher.pubOutMxdEvent(guAwardPos, new BaseEventParam(gu.getId(), WayEnum.MXD, rd));
		return rd;
	}

	@Override
	public String getTipCodeForAlreadyHandle() {
		return "city.mx.already.go";
	}

	/**
	 * 是否激活建筑功能
	 *
	 * @param gu
	 * @return
	 */
	private boolean isActive(GameUser gu) {
		boolean isActive = userCityService.isOwnLowNightmareCityAsCountry(gu.getId(), TypeEnum.Water.getValue());
		return isActive && gu.getStatus().ifNotInFsdlWorld();
	}
}