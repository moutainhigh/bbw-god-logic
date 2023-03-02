package com.bbw.god.server.guild.service;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.mall.store.AbstractStoreProcessor;
import com.bbw.god.mall.store.RDStore;
import com.bbw.god.mall.store.RDStoreGoodsInfo;
import com.bbw.god.mall.store.RDStoreGoodsInfo.BuyType;
import com.bbw.god.mall.store.StoreEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.GuildRD;
import com.bbw.god.server.guild.GuildShop;
import com.bbw.god.server.guild.GuildTools;
import com.bbw.god.server.guild.UserGuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月16日 上午9:14:11 
* 类说明 
*/
@Service
public class UserGuildStoreProgress extends AbstractStoreProcessor {
	@Autowired
	private GuildUserService guildUserService;
	@Autowired
	private GuildAwardService guildAwardService;
	@Override
	public boolean isMatch(int mallType) {
		return StoreEnum.GUILD.getType() == mallType;
	}

	@Override
	public RDStore getGoodsList(long guId) {
		RDStore rd = new RDStore();
		List<RDStoreGoodsInfo> goodsList = new ArrayList<RDStoreGoodsInfo>();
		List<GuildShop> list = GuildTools.getCfgGuildShops();
		Optional<UserGuild> userGuildOp = guildUserService.getUserGuildOp(guId);
		int lv = 0;
		if (userGuildOp.isPresent() && userGuildOp.get().getGuildId()!=0l){
			lv=userGuildOp.get().getGuildLv();
		}
		for (GuildShop goods : list) {
			RDStoreGoodsInfo info = new RDStoreGoodsInfo();
			info.setRealId(goods.getGoodId());
			info.setItem(goods.getPropType());
			info.setMallId(goods.getId());
			info.setParamInt(goods.getStar());
			BuyType type = new BuyType();
			type.setBoughtTimes(goods.getBought());
			type.setLimit(goods.getLimitCount());
			type.setConsume(goods.getPriceType());
			type.setPrice(goods.getPrice());
			info.setQuantity(goods.getQuantity());
			info.addBuyType(type);
			if (lv > 0) {
				if (goods.getMinLevel() > lv) {
					// 未解锁
					type.setPermit(goods.getPermit());
				} else {
					if (goods.getLimitCount() > 0 && userGuildOp.isPresent()) {
						type.setBoughtTimes(userGuildOp.get().getShopBought(goods.getId()));
					}
				}
			}
			goodsList.add(info);
		}
		rd.setIntegralGoods(goodsList);
		rd.setCurrency(userTreasureService.getTreasureNum(guId, TreasureEnum.GUILD_CONTRIBUTE.getValue()));
		return rd;
	}

	@Override
	public RDCommon buyGoods(long uid, int mallId, int buyNum,Integer consume) {
		GuildRD rd = new GuildRD();
		GuildShop goods = GuildTools.getCfgGuildShopById(mallId);
		Optional<UserGuild> userGuildOp = guildUserService.getUserGuildOp(uid);
		if (!userGuildOp.isPresent()) {
			// 没有行会不可购买
			goodsIsLock();
		}
		UserGuild userGuild = userGuildOp.get();
		if (goods.getMinLevel() > userGuild.getGuildLv()) {
			goodsIsLock();
		}
		if (goods.getLimitCount() > 0) {
			// 限购
			int bought = userGuild.getShopBought(mallId);
			if (bought >= goods.getLimitCount()) {
				goodsBuyLimited();
			}
			userGuild.addShopBought(mallId);
		}
		guildAwardService.buyGoodsAward(goods, buyNum, uid, rd);
		gameUserService.updateItem(userGuild);
		return rd;
	}

}
