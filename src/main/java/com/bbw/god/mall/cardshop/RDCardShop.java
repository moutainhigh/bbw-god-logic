package com.bbw.god.mall.cardshop;

import java.util.List;

import com.bbw.god.mall.cardshop.RDCardShop.RDCardPoolStatus;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 卡牌屋数据
 * 
 * @author suhq
 * @date 2019-05-08 08:55:45
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardShop extends RDSuccess {
	// 仙缘榜活动剩余时间
	private Integer xybRemainTime = -1;
	// 卡池状态
	private List<RDCardPoolStatus> cardPoolStatus;
	@Data
	public static class RDCardPoolStatus {
		private Integer cardPool;
		private Integer status;
		// 源晶活动剩余时间
		private Integer yjRemainTime = -1;

		public static RDCardPoolStatus instance(Integer cardPool, Integer status) {
			RDCardPoolStatus rd = new RDCardPoolStatus();
			rd.setCardPool(cardPool);
			rd.setStatus(status);
			return rd;
		}
	}

}
