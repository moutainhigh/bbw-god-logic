package com.bbw.god.city.chengc.trade;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 城池交易买入特产
 *
 * @author suhq
 * @date 2019年3月12日 下午2:35:33
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyGoodInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 购买物品ID **/
	private Integer goodId;
	/** 物品价格 */
	private Integer price;

	public BuyGoodInfo(Integer goodId, Integer price) {
		this.goodId = goodId;
		this.price = price;
	}

}
