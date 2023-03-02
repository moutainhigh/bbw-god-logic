package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.UserCitySetting;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ele.EVEle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 矿场
 *
 * @author suhq
 * @date 2018年11月29日 下午3:22:09
 */
public class KuangC extends Building {
	private static final int MAX_ENABLE_KC_NUM = 5;

	/**
	 * 领取建筑物奖励前的检查
	 */
	@Override
	protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {
		if (0 == userCity.getKc()) {
			rd.setMessage(LM.I.getMsg("city.cc.in.not.update"));
		}
	}

	public KuangC(UserCity userCity) {
		this.userCity = userCity;
		this.bType = BuildingEnum.KC;
	}

	@Override
	public RDBuildingInfo getBuildingInfo() {
		RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getKc(), getNextLevelInfo());
		buildingInfo.setKcNum(getKCNum(userCity.getKc()));
		buildingInfo.setEles(getAbleEles(userCity));
		buildingInfo.setRemainTimes(1);
		return buildingInfo;
	}

	@Override
	protected String getNextLevelInfo() {
        int nextBuildingLevel = userCity.getKc() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        if (nextBuildingLevel <= 4) return "开放一个新元素";
        if (nextBuildingLevel == 5) return "2个元素";
        if (nextBuildingLevel == 6) return "2~3个元素";
        if (nextBuildingLevel == 7) return "3个元素";
        if (nextBuildingLevel == 8) return "3~4个元素";
        if (nextBuildingLevel == 9) return "4个元素";
        if (nextBuildingLevel == 10) return "5个元素";

		return "";
	}

	@Override
	protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		userCity.setKc(userCity.getKc() + 1);
		RDBuildingInfo buildingInfo = getBuildingInfoInCache(gu, rdArriveChengC);
		// 解锁新元素
		if (userCity.getKc() <= 5) {
			int ele = 0;
			if (userCity.getKc() == 1) {
				ele = gu.gainCurCity().getProperty();
				buildingInfo.setEles(Arrays.asList(ele));
			} else {
				List<Integer> ableEles = buildingInfo.getEles();
				do {
					ele = PowerRandom.getRandomBySeed(5) * 10;
				} while (ableEles.contains(ele));
				ableEles.add(ele);
			}
			rd.setEle(ele);
		}
		// 新的可收取的数量
		int kcNum = getKCNum(userCity.getKc() + 1);
		buildingInfo.setKcNum(kcNum);
		rd.setKcNum(kcNum);
	}

	@Override
	protected int getNeedCopperForUpdate() {
		return 1000 * (int) Math.pow(2, userCity.getKc());
	}

	@Override
	public void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {
		RDBuildingInfo rdBuildingInfo = getBuildingInfo();
		List<Integer> ableEles = rdBuildingInfo.getEles();// 可产出元素

		List<Integer> awardEles = new ArrayList<>();

		if (param == null) {// 自动收取
			int kcNum = getKCNum(userCity.getKc());
			// TODO:可能存在空指针异常gameUserService.getSingleItem
			List<Integer> defaultKcEles = gameUserService.getSingleItem(gu.getId(), UserCitySetting.class)
					.getDefaultKcEles();
			for (int i = 0; i < defaultKcEles.size(); i++) {
				if (awardEles.size() < kcNum && ableEles.contains(defaultKcEles.get(i))) {
					awardEles.add(defaultKcEles.get(i));
				}
			}
		} else {// 手动收取
			awardEles = ListUtil.parseStrToInts(param);
			for (int i = 0; i < awardEles.size(); i++) {
				if (!ableEles.contains(awardEles.get(i))) {
					throw new ExceptionForClientTip("city.cc.in.kc.not.get");
				}
			}
		}

		int addedNum = 1;
		int rate = 1;
		// 土属性元素30%产出翻倍
		if (gu.gainCurCity().getProperty() == TypeEnum.Earth.getValue()) {
			int random = PowerRandom.getRandomBySeed(100);
			if (random <= 30) {
				rate = 2;
			}
		}
		// 暴击
		rate = getRate();
		if (hasDoubledAward(gu.getId())){
			rate+=2;
		}else if (isBanAward(gu.getId())){
			rate=0;
		}
		addedNum *= rate;
		rd.setRate(rate);
		if (monthLoginLogic.isExistEvent(gu.getId(),MonthLoginEnum.GOOD_KC) && PowerRandom.hitProbability(60)){
			addedNum*=2;
		}
		if (addedNum==0) {
			return;
		}
		// 发放元素
		final int addedEleFinal = addedNum;
		List<EVEle> eles = awardEles.stream().map(awardEle -> new EVEle(awardEle, addedEleFinal))
				.collect(Collectors.toList());
		ResEventPublisher.pubEleAddEvent(gu.getId(), eles, WayEnum.KC_AWARD, rd);
	}

	@Override
	protected String getAlreadyHandleTipCode() {
		return "city.cc.in.kc.already.get";
	}

	/**
	 * 玩家在矿产可选择的元素数
	 * 
	 * @param kcLevel
	 * @return
	 */
	private int getKCNum(int kcLevel) {
		if (kcLevel < 5) return 1;
		if (kcLevel == 5) return 2;
		if (kcLevel == 6) return PowerRandom.getRandomBySeed(2) == 1 ? 2 : 3;
		if (kcLevel == 7) return 3;
		if (kcLevel == 8) return PowerRandom.getRandomBySeed(2) == 1 ? 3 : 4;
		if (kcLevel == 9) return 4;
		else return 5;
	}

	/**
	 * 获得本次可能产出的元素
	 * 
	 * @param userCity
	 * @return
	 */
	private List<Integer> getAbleEles(UserCity userCity) {
		if (userCity.getKc() == 0) {
			return null;
		}
		List<Integer> ableEles = new ArrayList<>();
		ableEles.add(userCity.gainCity().getProperty());
		// 最大可产出的种类
		int maxEnableSize = userCity.getKc() > MAX_ENABLE_KC_NUM ? MAX_ENABLE_KC_NUM : userCity.getKc();
		for (int i = 1; i < maxEnableSize; i++) {
			int random = 0;
			do {
				random = PowerRandom.getRandomBySeed(5) * 10;
			} while (ableEles.contains(random));
			ableEles.add(random);
		}
		return ableEles;
	}

}
