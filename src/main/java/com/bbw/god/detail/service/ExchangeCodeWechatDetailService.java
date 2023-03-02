package com.bbw.god.detail.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.detail.entity.ExchangeCodeWechatDetailEntity;
import com.bbw.god.uac.entity.ExchangeCodeEntity;

/**
 * 礼包兑换记录
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-28 11:36:37
 */
public interface ExchangeCodeWechatDetailService extends IService<ExchangeCodeWechatDetailEntity> {
	/**
	 * 已经分发
	 * @param codeEntity
	 * @param serverId
	 * @param gameuserId
	 * @return
	 */
	boolean dispatched(ExchangeCodeEntity codeEntity, int serverId, long gameuserId);

	/**
	 * 分发微信每周礼包
	 * @param codeEntity
	 * @param serverId
	 * @param gameuserId
	 * @return
	 */
	boolean dispatch(ExchangeCodeEntity codeEntity, int serverId, long gameuserId);

	/**
	 * 清除过期的缓存
	 */
	void cacheOverTime();
}
