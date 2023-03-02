package com.bbw.god.db.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.CfgChannelDao;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.game.config.Cfg;

@Service
public class CfgChannelServiceImpl extends ServiceImpl<CfgChannelDao, CfgChannelEntity> implements CfgChannelService {

	@Override
	public Optional<CfgChannelEntity> getByPlatCode(String platCode) {
		List<CfgChannelEntity> channels = Cfg.I.get(CfgChannelEntity.class);
		Optional<CfgChannelEntity> entity = channels.stream().filter(value -> value.getPlatCode().equals(platCode)).findFirst();
		return entity;
	}

}
