package com.bbw.god.db.service;

import java.util.Optional;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.CfgChannelEntity;

/**
 * 玩家基础数据
 * 
 * @author suhq
 * @date 2018年11月28日 上午11:27:44
 */
public interface CfgChannelService extends IService<CfgChannelEntity> {
	/**
	 * 根据客户端渠道编码获取渠道对象
	 * @param platCode
	 * @return
	 */
	Optional<CfgChannelEntity> getByPlatCode(String platCode);
}
