package com.bbw.god.city.chengc;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.UserCityService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.TransmigrationCityRecordService;
import com.bbw.god.game.transmigration.TransmigrationLogic;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.guide.v1.NewerGuideEnum;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.changeworld.ChangeWorldStatistic;
import com.bbw.god.gameuser.statistic.behavior.changeworld.ChangeWorldStatisticService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 城池
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Service
public class ChengCProcessor implements ICityArriveProcessor {
	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.CC1, CityTypeEnum.CC2, CityTypeEnum.CC3,
			CityTypeEnum.CC4, CityTypeEnum.CC5);
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	protected UserCacheService userCacheService;
	@Autowired
	private UserCityService userCityService;
	@Autowired
	private ChangeWorldStatisticService changeWorldStatisticService;
	@Autowired
	private TransmigrationLogic transmigrationLogic;
	@Autowired
	private GameTransmigrationService gameTransmigrationService;
	@Autowired
	private TransmigrationCityRecordService transmigrationCityRecordService;

	private static final Date CLOSE_NIGHTMARE_TIME = DateUtil.fromDateTimeString("2099-01-15 14:01:00");

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return this.cityTypes;
	}

	@Override
	public Class<RDArriveChengC> getRDArriveClass() {
		return RDArriveChengC.class;
	}

	/**
	 * 到达城池：
	 * @param gu
	 * @param city
	 * @param rd
	 * @return
	 */
	@Override
	public RDArriveChengC arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd) {
		ChengChiInfoCache cache=ChengChiInfoCache.instance(city);
		RDArriveChengC rdInfo = new RDArriveChengC();
		UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
		cache.updateByUserCity(userCity);
		rdInfo.setOwnCity(0);
		rdInfo.setHierarchy(0);
		if (gu.getStatus().intoNightmareWord()){
			UserNightmareCity nightmareCity = userCityService.getUserNightmareCity(gu.getId(), city.getId());
			if (nightmareCity != null && nightmareCity.isOwn()) {
				rdInfo.setOwnCity(1);
				rdInfo.setHierarchy(userCity.getHierarchy());
			} else {
				cache.setLevelProgress(new int[1]);
			}
			if (nightmareCity != null && nightmareCity.getProcess() != null) {
				cache.setLevelProgress(nightmareCity.getProcess());
			}
			cache.setCityBuff(CityTool.getCityBuff(cache.getCityId()));
			cache.setArea(city.getCountry());
		} else if (gu.getStatus().ifInTransmigrateWord()) {
			rdInfo.setOwnCity(1);
			rdInfo.setHierarchy(userCity.getHierarchy());
			int sgId = gameUserService.getActiveGid(gu.getId());
			GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
			gameTransmigrationService.checkTransmigration(curTransmigration);
			Integer score = transmigrationCityRecordService.getScore(curTransmigration, gu.getId(), city.getId());
			rdInfo.setTransmigrationScore(score);

		} else {
			if (userCity != null && userCity.isOwn()) {
				rdInfo.setOwnCity(1);
				rdInfo.setHierarchy(userCity.getHierarchy());
			}
			if (userCity != null && userCity.getProcess() != null) {
				cache.setLevelProgress(userCity.getProcess());
			}
			cache.setArea(city.getCountry());
		}
		TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(),cache);
		return rdInfo;
	}

	/**
	 * 世界跳转（原世界->梦魇世界，梦魇世界->原世界）
	 *
	 * @param uid
	 * @return
	 */
	public RDChangeWorld changeWorld(long uid, Integer newWorldType) {
//		if (DateUtil.now().after(CLOSE_NIGHTMARE_TIME) && app.runAsProd()) {
//			throw new ExceptionForClientTip("nightmare.not.open");
//		}
		GameUser gu = gameUserService.getGameUser(uid);
		// 当前世界类型
		Integer wordType = gu.getStatus().getCurWordType();
		// 封神大陆世界未统一不能跳转
		if (WorldType.NORMAL.getValue() == (wordType)) {
			List<UserCity> userCities = userCityService.getUserCities(uid);
			if (userCities.size() != CityTool.getCcCount()) {
				throw new ExceptionForClientTip("normal.world.not.unite");
			}
		}
		// 设置并保存
		gu.getStatus().setPreWordType(wordType);
		gu.getStatus().setCurWordType(newWorldType);
		gu.updateStatus();
		// 根据跳转统计判断，第一次跳转梦魇的话，跳转到特定位置
		if (newWorldType == WorldType.NIGHTMARE.getValue()) {
			ChangeWorldStatistic statistic = changeWorldStatisticService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
			if (0 == statistic.getTotal()) {
				NewerGuideEnum guideEnum = NewerGuideEnum.START;
				gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
			}
		}
		RDChangeWorld rd = new RDChangeWorld(gu);
		if (newWorldType == WorldType.TRANSMIGRATION.getValue()) {
			List<Integer> mainCityType = transmigrationLogic.enter(uid);
			rd.setMainCityDefenderTypes(mainCityType);
		}
		// 发布世界跳转事件
		BaseEventParam bep = new BaseEventParam(uid, WayEnum.CHANGE_WORLD, new RDCommon());
		ChangeWorldEventPublisher.pubChangeWorldEvent(wordType, newWorldType, bep);
		return rd;
	}

}
