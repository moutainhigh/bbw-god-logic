package com.bbw.god.city;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.RDArriveChengC;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTypeEnum;

/**
 * 城市相关的逻辑检察器
 * 
 * @author suhq
 * @date 2018年11月24日 下午7:40:26
 */
public class CityChecker {

	/**
	 * 检查当前是否在城市中
	 * 
	 * @param city
	 */
	public static void checkIsCity(CfgCityEntity city, CityTypeEnum cType) {
		if (city.getType() != cType.getValue()) {
			throw new ExceptionForClientTip("city.not.here", city.getName());
		}
	}

	/**
	 * 检查当前是否在城池中
	 * 
	 * @param city
	 */
	public static void checkIsCC(CfgCityEntity city) {
		if (!city.isCC()) {
			throw new ExceptionForClientTip("city.not.here", city.getName());
		}
	}

	/**
	 * 检查当前是否在城池中
	 */
	public static void checkIsOwnCC(UserCity userCity) {
		if (userCity == null) {
			throw new ExceptionForClientTip("city.cc.not.own");
		}
	}

	/**
	 * 检查当前是否可振兴
	 *
	 */
	public static void checkIsAblePromote(UserCity userCity) {
		if (!userCity.ifAblePromote()) {
			throw new ExceptionForClientTip("city.cc.not.promote");
		}
	}

	public static void checkTraining(RDArriveChengC rdArriveChengC) {
		if (null == rdArriveChengC || 1 == rdArriveChengC.getToTraining()) {
			throw new ExceptionForClientTip("city.cc.already.training");
		}
	}

	public static void checkPromote(RDArriveChengC rdArriveChengC) {
		if (null == rdArriveChengC || 1 == rdArriveChengC.getToPromote()) {
			throw new ExceptionForClientTip("city.cc.already.promote");
		}
	}
}
