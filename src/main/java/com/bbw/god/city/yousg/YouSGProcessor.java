package com.bbw.god.city.yousg;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.AbstractSpecialCityProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidayBuGeiTangProcessor;
import com.bbw.god.activity.holiday.processor.holidayspecialcity.HolidaySpecialCityFactory;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.ICityHandleProcessor;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.BusinessGangService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.SpecialChecker;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游商馆 - 提供3个随机特产
 * 
 * @author suhq
 * @date 2018年10月24日 下午5:47:31
 */
@Component
public class YouSGProcessor implements ICityArriveProcessor, ICityHandleProcessor {
	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.YSG);

	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserSpecialService userSpecialService;
	@Autowired
	private HolidayBuGeiTangProcessor holidayBuGeiTangProcessor;
	@Autowired
	private HolidaySpecialCityFactory holidaySpecialBulidFactory;
	@Autowired
	private BusinessGangService businessGangService;

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return cityTypes;
	}

	@Override
	public Class<RDArriveYouSG> getRDArriveClass() {
		return RDArriveYouSG.class;
	}

	@Override
	public RDArriveYouSG arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
		//活动事件
		activityEvent(gameUser.getId(), rd);
		List<Integer> speciaIds = PowerRandom.getRandomInts(25, 3);
		//商帮效果特产升阶
		ArrayList<Integer> finalSpecialIds = new ArrayList<>();
		for (Integer specialId : speciaIds) {
			finalSpecialIds.add(businessGangService.specialUpgrade(gameUser, specialId));
		}
		RDArriveYouSG rdArriveYouSG = new RDArriveYouSG();
		rdArriveYouSG.setSpecials(finalSpecialIds);
		return rdArriveYouSG;
	}

	/**
	 * 活动事件
	 *
	 * @param uid
	 * @param rd
	 */
	private void activityEvent(long uid, RDAdvance rd) {
		AbstractSpecialCityProcessor specialBuildProcessor = holidaySpecialBulidFactory.getSpecialCityProcessor(uid);
		if (null == specialBuildProcessor) {
			return;
		}
		specialBuildProcessor.youSGTriggerEvent(uid, rd);
	}

	@Override
	public void checkIsHandle(GameUser gu, Object param) {
		return;
	}

	@Override
	public RDCommon handleProcessor(GameUser gu, Object param) {
		RDCommon rd = new RDCommon();
		List<Integer> specialIds = ListUtil.parseStrToInts((String) param);
		// 是否超上限
		SpecialChecker.checkIsFull(specialIds.size(),gu.getId());
		// 是否已卖完
		List<Integer> remainIds = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass()).getSpecials();
		for (int i = 0; i < specialIds.size(); i++) {
			if (!remainIds.contains(specialIds.get(i))) {
				throw new ExceptionForClientTip("city.ysg.already.soldOut");
			}
		}

		// 需要的铜钱
		long needCopper = ListUtil.sumInt(specialIds.stream()
				.map(specialId -> SpecialTool.getSpecialById(specialId).getPrice()).collect(Collectors.toList()));
		// 铜钱是否足够
		ResChecker.checkCopper(gu, needCopper);
		ResEventPublisher.pubCopperDeductEvent(gu.getId(), needCopper, WayEnum.YSG, rd);

		List<EVSpecialAdd> evSpecialAdds = specialIds.stream().map(specialId -> new EVSpecialAdd(specialId))
				.collect(Collectors.toList());
		SpecialEventPublisher.pubSpecialAddEvent(gu.getId(), evSpecialAdds, WayEnum.YSG, rd);

		// 更新缓存
		for (int i = 0; i < remainIds.size(); i++) {
			if (specialIds.contains(remainIds.get(i))) {
				remainIds.set(i, 0);
			}
		}

		return rd;
	}

	@Override
	public void setHandleStatus(GameUser gu, Object param) {
		return;
	}

	@Override
	public String getTipCodeForAlreadyHandle() {
		return null;
	}

}
