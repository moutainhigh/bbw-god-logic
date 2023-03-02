package com.bbw.god.gameuser.special;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 特产商情
 * 
 * @author suhq
 * @date 2019年3月11日 下午2:45:45
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSpecialBuinessInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<RDSpecialPrice> specialCities = null;// 系统特产出售城池的价格
	private List<RDSpecialPrice> sellingCities = null;// 玩家特产卖出的城池价格

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDSpecialPrice implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer cityId = null;
		private Integer price = null;

	}

}
