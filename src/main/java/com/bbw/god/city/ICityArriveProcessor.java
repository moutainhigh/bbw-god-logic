package com.bbw.god.city;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;

/**
 * 位置到达处理接口
 *
 * @author suhq
 * @date 2018年10月24日 下午5:53:20
 */
public interface ICityArriveProcessor extends ICityMatcher {

	/**
	 * 处理到达一座功能建筑要处理的业务
	 *
	 * @param gu
	 * @param city
	 * @param rd
	 */
	RDCityInfo arriveProcessor(GameUser gu, CfgCityEntity city, RDAdvance rd);

	/**
	 * 是否是新手引导的处理器
	 *
	 * @return
	 */
	default boolean isNewerGuideProcessor() {
		return false;
	}
}
