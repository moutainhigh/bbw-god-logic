package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.god.db.dao.WanXianRankDao;
import com.bbw.god.db.entity.WanXianRankEntity;
import com.bbw.god.db.service.WanXianRankService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WanXianRankServiceImpl extends ServiceImpl<WanXianRankDao, WanXianRankEntity> implements WanXianRankService {
	@Override
	public List<Integer> getAllSeasons() {
		EntityWrapper<WanXianRankEntity> wrapper = new EntityWrapper<>();
		wrapper.setSqlSelect(" distinct season");
		List<WanXianRankEntity> list = this.selectList(wrapper);
		return list.stream().map(WanXianRankEntity::getSeason).collect(Collectors.toList());
	}

	@Override
	public List<WanXianRankEntity> getNormalDataBySeason(int season) {
		EntityWrapper<WanXianRankEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("season", season).eq("wx_type", 1000);
		return this.selectList(wrapper);
	}

	@Override
	public List<WanXianRankEntity> getSpecialDataBySeason(int season) {
		EntityWrapper<WanXianRankEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("season", season).ne("wx_type", 1000);
		return this.selectList(wrapper);
	}
}
