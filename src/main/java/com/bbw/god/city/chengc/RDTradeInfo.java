package com.bbw.god.city.chengc;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.special.CfgSpecialHierarchyMap;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 进入交易获取交易特产信息
 * 
 * @author suhq
 * @date 2019年3月11日 下午2:45:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDTradeInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<RDSellingSpecial> sellingSpecials;// 玩家可卖出的特产
	private List<RDCitySpecial> citySpecials;// 城市出售特产
	private Integer discount;// 特产铺折扣
	private Integer premiumRate = 0;// 特产铺溢价

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDCitySpecial implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer id = null;// 特产ID
		private Integer level = null;// 特产铺等级
		private Integer status = null;// 是否哦解锁

	}

	@Getter
	@Setter
	@ToString
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDSellingSpecial implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long usId = null;
		private Integer id = null;// 特产ID
		private Integer boughtDiscount = null;// 买入时的折扣
		private Integer sellingPrice = null;// 召唤师出售价
		private Integer boughtPrice =null;//买入时的价格
		private Integer type= 0;//特产是否上锁  1是上锁 0是没上锁
		private SpecialSellPriceParam sellPriceParam;

		public static RDSellingSpecial fromUserSpecial(UserSpecial us, int sellingPrice,boolean lock) {
			//System.out.println(us.getId());
			RDSellingSpecial rdSellingSpecial = new RDSellingSpecial();
			rdSellingSpecial.setUsId(us.getId());
			rdSellingSpecial.setId(us.getBaseId());
			rdSellingSpecial.setBoughtDiscount(us.getDiscount());
			rdSellingSpecial.setSellingPrice(sellingPrice);
			rdSellingSpecial.setType(lock?1:0);
			//System.out.println(rdSellingSpecial.getUsId());
			return rdSellingSpecial;
		}

		/**
		 * 检查上锁状态
		 *
		 * @return
		 */
		public boolean checkLock() {
			return type == 1;
		}

		/**
		 * 是否盈利
		 *
		 * @return
		 */
		public boolean ifProfit() {
			if (boughtPrice == null) {
				return true;
			}
			return sellingPrice>boughtPrice;
		}
	}

}

