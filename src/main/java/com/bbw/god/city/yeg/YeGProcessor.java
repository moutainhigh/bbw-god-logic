package com.bbw.god.city.yeg;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.AbstractSpecialYeGuaiProcessor;
import com.bbw.god.activity.holiday.processor.holidayspcialyeguai.HolidaySpecialYeGuaiFactory;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.ICityArriveProcessor;
import com.bbw.god.city.yed.RDYeDEventCache;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 矿山、森林、湖泊、火山、泥沼
 *
 * @author suhq
 * @date 2018年10月24日 下午5:51:53
 */
@Slf4j
@Service
public class YeGProcessor implements ICityArriveProcessor {

	private List<CityTypeEnum> cityTypes = Arrays.asList(CityTypeEnum.KS, CityTypeEnum.SL, CityTypeEnum.HP, CityTypeEnum.HuoS, CityTypeEnum.NZ);
	@Autowired
	private YeGFightProcessorFactory yeGFightProcessorFactory;
	@Autowired
	private GameTransmigrationService gameTransmigrationService;
	@Autowired
	private HolidaySpecialYeGuaiFactory holidaySpecialYeGuaiFactory;

	@Override
	public List<CityTypeEnum> getCityTypes() {
		return this.cityTypes;
	}

	@Override
	public Class<RDArriveYeG> getRDArriveClass() {
		return RDArriveYeG.class;
	}

	@Override
	public RDArriveYeG arriveProcessor(GameUser gameUser, CfgCityEntity city, RDAdvance rd) {
		IYegFightProcessor fightProcessor = yeGFightProcessorFactory.randomYeGFightProcessor(gameUser);
		int yeGType = fightProcessor.getYeGEnum().getType();
		RDYeDEventCache yeDEventCache = TimeLimitCacheUtil.getYeDEventCache(gameUser.getId());
		Set<Integer> eventIds = yeDEventCache.getEventIds();
		if (eventIds.contains(YdEventEnum.JIN_WEI_JUN.getValue())) {
			fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(YeGuaiEnum.YG_ELITE);
			eventIds.remove(YdEventEnum.JIN_WEI_JUN.getValue());
			TimeLimitCacheUtil.setYeDEventCache(gameUser.getId(), yeDEventCache);
		}
		int type = getYeGType(gameUser, city);
		RDFightsInfo rdFightsInfo = fightProcessor.getFightsInfo(gameUser, type);
		// 初始战斗为未结算
		TimeLimitCacheUtil.removeCache(gameUser.getId(), RDFightResult.class);
		RDArriveYeG rdArriveYeG = RDArriveYeG.getInstance(rdFightsInfo, city,
				fightProcessor.getAdditionGoal().getVal(), type);
		rdArriveYeG.setYeGuaiType(fightProcessor.getYeGEnum().getType());

		if (yeGType == YeGuaiEnum.YG_NORMAL.getType() || yeGType == YeGuaiEnum.YG_ELITE.getType()) {
			//活动特殊野怪
			AbstractSpecialYeGuaiProcessor specialYeGuaiProcessor = holidaySpecialYeGuaiFactory.getSpecialYeGuaiProcessor(gameUser.getId());
			if (null != specialYeGuaiProcessor) {
				specialYeGuaiProcessor.changeFightInfo(gameUser.getId(), rdArriveYeG, rdFightsInfo);
			}
		}
		return rdArriveYeG;
	}

    /**
     * 野怪开箱子
     *
     * @param guId
     * @return
     */
    public RDCommon openBox(long guId) {
        RDFightEndInfo fightEndInfo = TimeLimitCacheUtil.getFightEndCache(guId);
        if (null == fightEndInfo) {
            log.error("-------------{}打野失败，尝试开箱子--------------", guId);
            throw new ExceptionForClientTip("yg.cannot.openbox");
        }
		if (fightEndInfo.getYeGtype() == null) {
			fightEndInfo.setYeGtype(YeGuaiEnum.YG_NORMAL);
		}
		IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(fightEndInfo.getYeGtype());
		return fightProcessor.openBox(fightEndInfo, guId);
    }

	/**
	 * 获取对应宝箱内 的随机奖励
	 *
	 * @param guId
	 * @param yeGuaiEnum
	 * @return
	 */
	public List<Award> getRandomBoxAwards(long guId, YeGuaiEnum yeGuaiEnum) {
		IYegFightProcessor fightProcessor = yeGFightProcessorFactory.makeYeGFightProcessor(yeGuaiEnum);
		return fightProcessor.getRandomBoxAwards(guId);
	}

	/**
	 * 获取野怪属性
	 *
	 * @param user
	 * @param city
	 * @return
	 */
	private int getYeGType(GameUser user, CfgCityEntity city) {
		int type = city.getType() - 100;
		//轮回世界随机属性
		if (user.getStatus().ifInTransmigrateWord()) {
			GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(ServerTool.getServerGroup(user.getServerId()));
			if (null == curTransmigration) {
				return type;
			}
			int mainCityType = curTransmigration.getMainCityDefenderTypes().get(city.getCountry() / 10 - 1);
			type = TypeEnum.getRandomNotCounterattackType(mainCityType);
		}
		return type;
	}
}
