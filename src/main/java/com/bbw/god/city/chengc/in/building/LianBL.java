package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgLianBL;
import com.bbw.god.game.config.CfgLianBL.LianBLTreasure;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 炼宝炉
 * 
 * @author suhq
 * @date 2018年11月29日 下午3:23:58
 */
public class LianBL extends Building {

	public LianBL(UserCity userCity) {
		this.userCity = userCity;
		this.bType = BuildingEnum.LBL;
	}

	@Override
	public RDBuildingInfo getBuildingInfo() {
		RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getLbl(), getNextLevelInfo());
		buildingInfo.setRemainTimes(1);
		return buildingInfo;
	}

	@Override
	public String getNextLevelInfo() {
        int nextBuildingLevel = userCity.getLbl() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        return "提高法宝灵石的掉率";
    }

	@Override
	protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		userCity.setLbl(userCity.getLbl() + 1);
	}

	@Override
	protected int getNeedCopperForUpdate() {
		return 5000 * (int) Math.pow(2, userCity.getLbl());
	}

	@Override
	public void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {
		boolean additional=false;
		if (monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.GOOD_LBL) && PowerRandom.hitProbability(10)){
			TreasureEventPublisher.pubTAddEvent(gu.getId(), getTreasure(userCity), 1, WayEnum.LBL_AWARD, rd);
			additional=true;
		}
		boolean isAbleGet = isAbleGetTreasure(gu.getId(), userCity.getLbl());
		if (!isAbleGet) {
			if (!additional){
				rd.setMessage(LM.I.getMsg("city.cc.in.lbl.not.output"));
			}
			return;
		}
		int treasureId = getTreasure(userCity);
		int num=1;
		if (hasDoubledAward(gu.getId())){
			num*=2;
		}else if (isBanAward(gu.getId())){
			return;
		}
		TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureId, num, WayEnum.LBL_AWARD, rd);
	}

	@Override
	protected String getAlreadyHandleTipCode() {
		return "city.cc.in.lbl.already.get";
	}

	/**
	 * 领取建筑物奖励前的检查
	 */
	@Override
	protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {
		if (0 == userCity.getLbl()) {
			rd.setMessage(LM.I.getMsg("city.cc.in.not.update"));
		}
	}

	/**
	 * 炼丹房是否可以获得法宝
	 *
	 * @param uid
	 * @param ldlLevel
	 * @return
	 */
	private boolean isAbleGetTreasure(long uid, int ldlLevel) {
		int random = PowerRandom.getRandomBySeed(100);
		if (ldlLevel == 1) {
			if (random <= 15) {
				return true;
			}
		} else if (ldlLevel < 9) {
			if (random <= (ldlLevel + 1) * 10) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	private Integer getTreasure(UserCity uc) {
		CfgCityEntity city = uc.gainCity();
		// 获得随机元宝的概率
		int tmp = userCity.getLbl() + city.getLevel();
		int rand3 = 20 + (tmp - 2) * 2;
		int rand4 = 0;
		int rand5 = 0;

		if (tmp > 2) {
			rand4 = tmp;
		}
		if (tmp > 3) {
			rand5 = tmp - 3;
		}
		// 获得星级
		int star = TreasureTool.getRandomStar(rand5, rand4, rand3);
		CfgLianBL config = Cfg.I.getUniqueConfig(CfgLianBL.class);
		// 获得可产出的道具
		Stream<LianBLTreasure> stream = config.getTreasures().stream();
		if (uc.getLbl() >= 6) {
			// 增加高级特产
			stream = Stream.concat(stream, config.getHighLevelTreasures().stream());
			// 增加灵石
			if (PowerRandom.getRandomBySeed(config.getTreasures().size() + config.getHighLevelTreasures().size()) == 1) {
				stream = Stream.concat(stream, config.getLingshis().stream());
			}
		}
		// 可产出的星级道具
		List<LianBLTreasure> treasures = stream.filter(t -> t.getStar() == star).collect(Collectors.toList());
		// 随机星级道具
		LianBLTreasure treasure = PowerRandom.getRandomFromList(treasures);
		return treasure.getId();
	}

}
