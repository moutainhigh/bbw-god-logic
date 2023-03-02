package com.bbw.god.db.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsErrorLogEntity;
import com.bbw.god.db.entity.WanXianUserCardsEntity;
import com.bbw.god.game.wanxianzhen.WanXianCard;

/**
 * 玩家竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:46:11
 */
public interface WanXianUserCardsService extends IService<WanXianUserCardsEntity> {
	public List<WanXianCard> getCardsFromDb(long uid,int season,int type);
}
