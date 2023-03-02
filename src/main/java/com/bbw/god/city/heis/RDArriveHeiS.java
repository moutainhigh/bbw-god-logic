package com.bbw.god.city.heis;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达黑市
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveHeiS extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDHeiSGoods> goods = null;// 可购买的商品
	
	@Data
	public static class RDHeiSGoods implements Serializable{
		private static final long serialVersionUID = 1L;
		private Integer goodsId=null;
		private Integer price=null;
		
		public static RDHeiSGoods instance(Integer goodsId,Integer price) {
			RDHeiSGoods rdGoods=new RDHeiSGoods();
			rdGoods.setGoodsId(goodsId);
			rdGoods.setPrice(price);
			return rdGoods;
		}
	}

}
