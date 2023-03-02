package com.bbw.god.exchange;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 可兑换的商品信息
 * 
 * @author suhq
 * @date 2019年3月12日 下午4:28:37
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDExchangeList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDExchangeGoodInfo> goods = null;
	private Integer ownSsNum = null;// 神沙拥有数
	private Integer ownHyNum = null;// 魂源拥有数量

	/**
	 * 可兑换的单个商品信息
	 * 
	 * @author suhq
	 * @date 2019年3月27日 上午11:34:45
	 */
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDExchangeGoodInfo implements Serializable {

		private static final long serialVersionUID = 1L;
		private Integer id = null;
		private Integer type = null;
		private Integer realId = null;
		private Integer paramInt = 0;
		private Integer quantity = null;
		private Integer unit = null;
		public static RDExchangeGoodInfo fromExchangeGood(CfgExchangeGoodEntity good) {
			RDExchangeGoodInfo rdGoodInfo = new RDExchangeGoodInfo();
			rdGoodInfo.setId(good.getId());
			rdGoodInfo.setType(good.getType());
			rdGoodInfo.setRealId(good.getGoodId());
			rdGoodInfo.setQuantity(good.getPrice());
			rdGoodInfo.setUnit(good.getUnit());
			return rdGoodInfo;
		}
	}

	/**
	 * 可兑换的单个商品信息(兼容老旧的诛仙阵和封神台)
	 * 
	 * @author suhq
	 * @date 2019年3月12日 下午4:27:21
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDOldExchangeGoodInfo extends RDExchangeGoodInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer goodId = null;
		private Integer pointForExchange = null;

		public RDOldExchangeGoodInfo(int goodId, int pointForExchange) {
			this.goodId = goodId;
			this.pointForExchange = pointForExchange;
		}
	}

}
