package com.bbw.god.mall.store;

import com.bbw.god.gameuser.treasure.UserTreasureService;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.rd.RDCommon;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月12日 下午2:46:01 类说明 积分商店
 */

public abstract class AbstractStoreProcessor {
	@Autowired
	protected GameUserService gameUserService;
	@Autowired
	protected UserTreasureService userTreasureService;
	public abstract boolean isMatch(int mallType);
	/**
	 * 获得商品列表
	 *
	 * @param guId
	 * @return
	 */
	public abstract RDStore getGoodsList(long guId);

	/**
	 * 购买商品
	 * @param uid
	 * @param mallId
	 * @param buyNum
	 * @param consume 单位（选传）
	 * @return
	 */
	public abstract RDCommon buyGoods(long uid, int mallId, int buyNum,Integer consume);

	/**
	 * 商品未解锁
	 */
	public void goodsIsLock() {
		throw new ExceptionForClientTip("store.goods.lock");
	}

	/**
	 * 商品购买次数上限
	 */
	public void goodsBuyLimited() {
		throw new ExceptionForClientTip("store.goods.limit");
	}
}
