package com.bbw.god.city;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;

/**
 * 建筑体验
 *
 * @author: suhq
 * @date: 2021/11/9 5:34 下午
 */
public interface ICityExpProcessor extends ICityMatcher {


	/**
	 * 地图建筑提前体验
	 *
	 * @param gu
	 * @return
	 */
	RDCityInfo exp(GameUser gu, CfgCityEntity city);
}
