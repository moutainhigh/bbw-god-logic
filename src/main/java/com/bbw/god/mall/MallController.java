package com.bbw.god.mall;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商城接口
 *
 * @author suhq
 * @date 2018年11月1日 上午10:17:11
 */
@RestController
public class MallController extends AbstractController {
	@Autowired
	private MallLogic mallLogic;
	@Autowired
	private CartBuyMallLogic cartBuyMallLogic;

	/**
	 * @param type 参见malltype
	 * @return
	 */
	@GetMapping(CR.Mall.LIST_MALLS)
	public RDMallList listMalls(int type) {
		return mallLogic.getProducts(getUserId(), type);
	}

	@GetMapping(CR.Mall.REFRESH_MYSTERIOUS)
	public RDMallList refreshMysteriousTreasures() {
		return mallLogic.refreshMysterious(getUserId());
	}

	@GetMapping(CR.Mall.REFRESH_MY_TREASURE_TROVE)
	public RDCommon refreshMyTreasureTrove() {
		return mallLogic.refreshMyTreasureTrove(getUserId());
	}

	@GetMapping(CR.Mall.BUY_MY_TREASURE_TROVE)
	public RDCommon buyMyTreasureTrove(Integer mallIndex) {
		if (null == mallIndex) {
			throw new ExceptionForClientTip("client.request.unvalid.arg");
		}
		if (mallIndex < 0 || mallIndex >= 9) {
			throw new ExceptionForClientTip("award.not.valid.choose");
		}
		return mallLogic.buyMyTreasureTrove(getUserId(), mallIndex);
	}

	@GetMapping(CR.Mall.BUY)
	public RDCommon buy(int proId, Integer buyNum) {
		if (buyNum == null) {
			buyNum = 1;
		}
		if (buyNum <= 0) {
			throw ExceptionForClientTip.fromi18nKey("buy.num.unvalid");
		}
		Integer value = Math.abs(Integer.valueOf(buyNum));
		return mallLogic.buy(getUserId(), proId, value);
	}

	/**
	 * 购物车结算
	 *
	 * @param malls // 结算请求信息 (结构) 记录id,商品id,数量;记录id,商品id:数量
	 */
	@GetMapping(CR.Mall.CART_BUY)
	public RDCommon cartBuy(String malls) {

		return cartBuyMallLogic.cartBuy(getUserId(), malls);
	}

	@GetMapping(CR.Mall.GET_MALL_INFO)
	public RDMallInfo getMallBagInfo(int proId) {
		return mallLogic.getMallBagInfo(getUserId(), proId);
	}

	@GetMapping(CR.Mall.GET_GOODS_INFO)
	public RDMallInfo getGoodsInfo(int type,int goodsId){
		return mallLogic.getGoodsInfoByTypeID(getUserId(), type, goodsId);
	}
}