package com.bbw.god.city;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDCommon;

/**
 * 到达后的操作接口，如客栈购买卡牌
 * 
 * @author suhq
 * @date 2018年10月24日 下午5:53:20
 */
public interface ICityHandleProcessor extends ICityMatcher {
	/**
	 * 是否已操作
	 * 
	 * @param gu
	 * @param param
	 */
	default public void checkIsHandle(GameUser gu, Object param) {
		RDCityInfo rdCityInfo = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass());
		if (rdCityInfo.getHandleStatus().equals("0")) {
			throw new ExceptionForClientTip(getTipCodeForAlreadyHandle());
		}
	}

	/**
	 * 城市的操作逻辑
	 * 
	 * @param gu
	 * @param param
	 * @return
	 */
	public RDCommon handleProcessor(GameUser gu, Object param);

	/**
	 * 标记已处理
	 * 
	 * @param gu
	 * @param param
	 */
	default public void setHandleStatus(GameUser gu, Object param) {
		RDCityInfo rdCityInfo = TimeLimitCacheUtil.getArriveCache(gu.getId(), getRDArriveClass());
		rdCityInfo.setHandleStatus("0");
		TimeLimitCacheUtil.setArriveCache(gu.getId(), rdCityInfo);
	}

	/**
	 * 已处理的提示码
	 * 
	 * @return
	 */
	public String getTipCodeForAlreadyHandle();
}
