package com.bbw.god.detail.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.DateUtil;
import com.bbw.god.detail.dao.ExchangeCodeWechatDetailDao;
import com.bbw.god.detail.entity.ExchangeCodeWechatDetailEntity;
import com.bbw.god.detail.service.ExchangeCodeWechatDetailService;
import com.bbw.god.uac.GodUACCache;
import com.bbw.god.uac.entity.ExchangeCodeEntity;

@Service("exchangeCodeWechatDetailService")
public class ExchangeCodeWechatDetailServiceImpl extends ServiceImpl<ExchangeCodeWechatDetailDao, ExchangeCodeWechatDetailEntity> implements ExchangeCodeWechatDetailService {

	private boolean exists(String id) {
		if (GodUACCache.wechatCode.containsKey(id)) {
			return true;
		}
		ExchangeCodeWechatDetailEntity entity = this.selectById(id);
		if (null == entity) {
			return false;
		} else {
			GodUACCache.put(GodUACCache.wechatCode, id, DateUtil.toDateString(entity.getValidTime()));
			return true;
		}
	}

	private ExchangeCodeWechatDetailEntity transform(ExchangeCodeEntity code, int serverId, long gameuserId, String id) {
		ExchangeCodeWechatDetailEntity entity = new ExchangeCodeWechatDetailEntity();
		entity.setCode(code.getCode());
		entity.setDispatchTime(DateUtil.now());
		entity.setGameuserId(gameuserId);
		entity.setId(id);
		entity.setPacks(code.getPacks());
		entity.setServerId(serverId);
		entity.setStatus(true);
		entity.setUser(code.getUser());
		entity.setValidTime(code.getValidTime());
		return entity;
	}

	@Override
	public boolean dispatch(ExchangeCodeEntity codeEntity, int serverId, long gameuserId) {
		String key = getKey(codeEntity, serverId, gameuserId);
		if (exists(key)) {
			return true;
		}
		ExchangeCodeWechatDetailEntity entity = transform(codeEntity, serverId, gameuserId, key);
		entity.setDispatchTime(DateUtil.now());
		GodUACCache.put(GodUACCache.wechatCode, key, DateUtil.toDateString(codeEntity.getValidTime()));
		return this.insert(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.bbw.god.detail.service.ExchangeCodeWechatDetailService#dispatched(com.bbw.god.uac.entity.ExchangeCodeEntity,
	 * int, long)
	 */
	@Override
	public boolean dispatched(ExchangeCodeEntity codeEntity, int serverId, long gameuserId) {
		String key = getKey(codeEntity, serverId, gameuserId);
		return exists(key);
	}

	private String getKey(ExchangeCodeEntity code, int serverId, long gameuserId) {
		//code,gameuserid,serverid,validtime的日期
		String key_tpl = "%s#%s#%s#%s";
		String key = String.format(key_tpl, code.getCode(), gameuserId, serverId, DateUtil.toDateInt(code.getValidTime()));
		return key;
	}

	/*
	 * (non-Javadoc)
	 * @see com.bbw.god.detail.service.ExchangeCodeWechatDetailService#cacheOverTime()
	 */
	@Override
	public void cacheOverTime() {
		GodUACCache.clear(GodUACCache.wechatCode);
	}

}
