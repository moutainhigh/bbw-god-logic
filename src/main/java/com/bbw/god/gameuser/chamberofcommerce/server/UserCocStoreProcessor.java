package com.bbw.god.gameuser.chamberofcommerce.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bbw.god.mall.store.RDStore;
import com.bbw.god.mall.store.StoreEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.game.config.CfgCoc.CocShop;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.chamberofcommerce.CocPrivilegeEnum;
import com.bbw.god.gameuser.chamberofcommerce.CocTools;
import com.bbw.god.gameuser.chamberofcommerce.RDCoc;
import com.bbw.god.gameuser.chamberofcommerce.UserCocInfo;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.store.RDStoreGoodsInfo;
import com.bbw.god.mall.store.RDStoreGoodsInfo.BuyType;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.store.AbstractStoreProcessor;
import com.bbw.god.rd.RDCommon;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月12日 上午10:28:08 类说明 商会商店逻辑
 */
@Service
public class UserCocStoreProcessor extends AbstractStoreProcessor {
	@Autowired
	private UserCocInfoService cocInfoService;
	@Autowired
	private UserCocStoreService userCocStoreService;

	@Override
	public boolean isMatch(int mallType) {
		return StoreEnum.COC.getType() == mallType;
	}

	@Override
	public RDStore getGoodsList(long guId) {
		RDStore rd = new RDStore();
		List<RDStoreGoodsInfo> goodsList = new ArrayList<RDStoreGoodsInfo>();
		Optional<UserCocInfo> infOptional = cocInfoService.getUserCocInfoOp(guId);
		int lv = 0;
		if (infOptional.isPresent()) {
			lv = infOptional.get().getHonorLevel();
		}
		List<CocShop> cocShops = userCocStoreService.getUserShopList(guId);
		for (CocShop goods : cocShops) {
			RDStoreGoodsInfo info = new RDStoreGoodsInfo();
			info.setRealId(goods.getGoodId());
			info.setItem(goods.getPropType());
			info.setMallId(goods.getId());
			BuyType type = new BuyType();
			type.setBoughtTimes(goods.getBought());
			type.setLimit(goods.getLimitCount());
			type.setConsume(goods.getPriceType());
			type.setPrice(goods.getPrice());
			info.addBuyType(type);
			if (goods.getMinLevel() > lv) {
				// 未解锁
				type.setPermit(goods.getPermit());
			}
			goodsList.add(info);
		}
		rd.setIntegralGoods(goodsList);
		rd.setCurrency(userTreasureService.getTreasureNum(guId, TreasureEnum.SHJB.getValue()));
		return rd;
	}

	@Override
	public RDCommon buyGoods(long uid, int mallId, int buyNum,Integer consume) {
		UserCocInfo info = cocInfoService.getUserCocInfo(uid);
		RDCoc rd = new RDCoc();
		Optional<CocShop> shopOp = CocTools.getCocShopById(mallId);
		if (!shopOp.isPresent()) {
			// 商品不存在
			throw new ExceptionForClientTip("coc.gift.not.exist");
		}
		CocShop shop = shopOp.get();
		if (shop.getMinLevel() > info.getHonorLevel()) {
			// 等级不够
			throw new ExceptionForClientTip("coc.level.toolower");
		}
		if (shop.getLimitCount() > 0) {
			// 说明该商品有购买次数限制
			CocPrivilegeEnum cocPrivilegeEnum = CocPrivilegeEnum.getPrivilegeEnumByShopId(shop.getId());
			int limit = CocTools.getAddByPrivilege(cocPrivilegeEnum, info.getHonorLevel());
			limit += shop.getLimitCount();
			if (info.CocShopLimted(mallId, limit)) {
				throw new ExceptionForClientTip("coc.buy.limt");
			}
		}
		// 扣除并下发
		if (shop.getPriceType() == ConsumeType.GOLD.getValue()) {
			// 如果消费类型为元宝则扣元宝
			ResChecker.checkGold(gameUserService.getGameUser(uid), shop.getPrice());
			ResEventPublisher.pubGoldDeductEvent(uid, shop.getPrice(), WayEnum.Chamber_Of_Commerce_SHOP, rd);
		} else {
			// 否则为商会金币
			TreasureChecker.checkIsEnough(TreasureEnum.SHJB.getValue(), shop.getPrice(), uid);
			TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SHJB.getValue(), shop.getPrice(),
					WayEnum.Chamber_Of_Commerce_SHOP, rd);
		}
		gameUserService.updateItem(info);
		// 发放物品
		if (shop.getPropType() == 40) {
			CardEventPublisher.pubCardAddEvent(uid, shop.getGoodId(), WayEnum.Chamber_Of_Commerce_SHOP, "商会商店购买", rd);
		} else {
			TreasureEventPublisher.pubTAddEvent(uid, shop.getGoodId(), 1, WayEnum.Chamber_Of_Commerce_SHOP, rd);
		}
		if (shop.getLimitCount() > 0) {
			// 限购产品 需要更新用户的今日购买
			info.addCocShopBought(mallId);
			gameUserService.updateItem(info);
		}
		return rd;
	}

}
