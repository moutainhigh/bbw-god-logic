package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.db.dao.InsAccessLogDao;
import com.bbw.god.db.dao.WanXianUserCardsDao;
import com.bbw.god.db.entity.InsAccessLogEntity;
import com.bbw.god.db.entity.WanXianUserCardsEntity;
import com.bbw.god.db.service.InsAccessLogService;
import com.bbw.god.db.service.WanXianUserCardsService;
import com.bbw.god.game.wanxianzhen.WanXianCard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WanXianUserCardsServiceImpl extends ServiceImpl<WanXianUserCardsDao, WanXianUserCardsEntity> implements WanXianUserCardsService {

	@Override
	public List<WanXianCard> getCardsFromDb(long uid, int season, int type) {
		EntityWrapper<WanXianUserCardsEntity> ew=new EntityWrapper<WanXianUserCardsEntity>();
		ew.eq("uid", uid).eq("season", season).eq("wxtype", type);
		WanXianUserCardsEntity entity=this.selectOne(ew);
		if (entity==null || StrUtil.isBlank(entity.getCards())) {
			return new ArrayList<WanXianCard>();
		}
		return JSONUtil.fromJsonArray(entity.getCards(), WanXianCard.class);
	}
	
	
}
