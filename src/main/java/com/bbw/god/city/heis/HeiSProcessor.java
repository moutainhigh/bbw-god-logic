package com.bbw.god.city.heis;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.city.heis.RDArriveHeiS.RDHeiSGoods;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgHeiS;
import com.bbw.god.game.config.CfgHeiS.HeiSTreasure;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 黑市 - 随机物件法宝
 * 
 * @author suhq
 * @date 2018年10月24日 下午5:53:55
 */
@Component
public class HeiSProcessor implements ICityArriveProcessor, ICityHandleProcessor {

	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.HEIS);

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return cityTypes;
	}

	@Override
	public Class<RDArriveHeiS> getRDArriveClass() {
		return RDArriveHeiS.class;
	}

	@Override
	public RDArriveHeiS arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
		List<RDHeiSGoods> heiSTreasures = getTreasuresToSell(3);
		RDArriveHeiS rdArriveHeiS = new RDArriveHeiS();
		rdArriveHeiS.setGoods(heiSTreasures);
		rdArriveHeiS.setHandleStatus("1");
		return rdArriveHeiS;

	}

	@Override
	public RDCommon handleProcessor(GameUser gu, Object param) {
		RDCommon rd = new RDCommon();
		int treasureId = (Integer) param;
		List<RDHeiSGoods> availableTreasures = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass())
				.getGoods();
		// 法宝是否有效
		if (availableTreasures == null) {
			throw new ExceptionForClientTip("city.heis.not.exist");
		}
		Optional<RDHeiSGoods> optional = availableTreasures.stream().filter(p -> p.getGoodsId() == treasureId).findFirst();
		if (!optional.isPresent()) {
			throw new ExceptionForClientTip("city.heis.not.exist");
		}
		long needCopper = getPrice(treasureId);
		// 铜钱是否足够
		ResChecker.checkCopper(gu, needCopper);
		ResEventPublisher.pubCopperDeductEvent(gu.getId(), needCopper, WayEnum.HEIS, rd);

		TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureId, 1, WayEnum.HEIS, rd);

		return rd;
	}

	@Override
	public String getTipCodeForAlreadyHandle() {
		return "city.heis.already.buy";
	}

	/**
	 * 黑市出售的num个特产
	 * 
	 * @param num
	 * @return
	 */
	private List<RDHeiSGoods> getTreasuresToSell(int num) {
		CfgHeiS config = Cfg.I.getUniqueConfig(CfgHeiS.class);
		List<HeiSTreasure> treasures = config.getTreasures();
		List<RDHeiSGoods> heiSTreasures = PowerRandom.getRandomsFromList(treasures, config.getNum()).stream()
				.map(p->{return RDHeiSGoods.instance(p.getId(), p.getPrice());}).collect(Collectors.toList());
		return heiSTreasures;
	}

	private int getPrice(int treasureId) {
		CfgHeiS config = Cfg.I.getUniqueConfig(CfgHeiS.class);
		List<HeiSTreasure> treasures = config.getTreasures();
		HeiSTreasure treasure = treasures.stream().filter(t -> t.getId() == treasureId).findFirst().orElse(null);
		if (treasure == null) {
			throw new ExceptionForClientTip("city.heis.not.exist");
		}
		return treasure.getPrice();
	}
}
