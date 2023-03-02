package com.bbw.god.city;

import com.bbw.god.game.config.city.CityTypeEnum;

import java.util.List;

/**
 * 接口匹配器
 *
 * @author suhq
 * @date 2018年10月24日 下午5:53:20
 */
public interface ICityMatcher {

	default boolean isMatch(CityTypeEnum type) {
		return getCityTypes().contains(type);
	}

	/**
	 * 是否是梦魇专用处理器
	 *
	 * @return
	 */
	default boolean isNightmare() {
		return false;
	}

	List<CityTypeEnum> getCityTypes();

	<T extends RDCityInfo> Class<T> getRDArriveClass();
}
