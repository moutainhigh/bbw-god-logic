package com.bbw.god.game.config.mall;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.Optional;

public class MallTool {

	public static List<CfgMallEntity> getGoods() {
		return Cfg.I.get(CfgMallEntity.class);
	}

	public static CfgMallEntity getMall(int mallId) {
		return Cfg.I.get(mallId, CfgMallEntity.class);
	}

	public static CfgMallExtraPackEntity getMallExtraPack(int mallId) {
		return Cfg.I.get(mallId, CfgMallExtraPackEntity.class);
	}

	public static CfgMallEntity getMall(int type, int goodsId) {
		Optional<CfgMallEntity> optional = getGoods().stream().filter(mall -> mall.getType() == type && mall.getGoodsId() == goodsId).findFirst();
		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}

	/**
	 * 从NOT_SHOWED、DJ中根据goodsId获得商品
	 * 
	 * @param goodsId
	 * @return
	 */
	public static CfgMallEntity getMallTreasure(int goodsId) {
		Optional<CfgMallEntity> optional = getGoods().stream().filter(mall -> mall.getType() <= MallEnum.DJ.getValue() && mall.getGoodsId() == goodsId).findFirst();
		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}

	public static CfgMall getMallConfig() {
		return Cfg.I.getUniqueConfig(CfgMall.class);
	}

	public static CfgMaouMallAuth getAuthMaouMallConfig(int goodsId) {
		CfgMaouMallAuth mallAuth = Cfg.I.get(goodsId, CfgMaouMallAuth.class);
		if (mallAuth == null) {
			throw CoderException.high("无效的魔王商店商品id：" + goodsId);
		}
		return mallAuth;
	}

	public static CfgSnatchTreasureMallCondition getSnatchTreasureMallConfig(int goodsId) {
		CfgSnatchTreasureMallCondition mall = Cfg.I.get(goodsId, CfgSnatchTreasureMallCondition.class);
		if (mall == null) {
			throw CoderException.high("无效的夺宝商店商品id：" + goodsId);
		}
		return mall;
	}

	/**
	 * 获得直冲ID
	 *
	 * @return
	 */
	public static int getRechargeId(int goodsId) {
		return 99000000 + goodsId;
	}

	/**
	 * 根据直冲ID获取商品
	 *
	 * @return
	 */
	public static int getGoodsId(int recharegeId) {
		return recharegeId - 99000000;
	}

	public static CfgMallEntity getAdventureMallTreasure(int goodsId) {
		Optional<CfgMallEntity> optional =
				getGoods().stream().filter(mall -> mall.getType() == MallEnum.ADVENTURE.getValue() && mall.getGoodsId() == goodsId).findFirst();
		return optional.orElse(null);
	}
}
