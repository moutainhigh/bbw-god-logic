package com.bbw.god.server.flx;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达富临轩
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveFuLX extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer ysgCard = null;// 押押乐开奖卡牌
	private Integer odds = null;//
	private List<RDSGBet> sgBets = null;// 大乐透投注记录
	private List<List<Integer>> ysgBets = null;// 数馆投注记录

	/**
	 * 富临轩 - 数馆投注
	 * 
	 * @author suhq
	 * @date 2018年10月30日 下午2:30:53
	 */
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDSGBet implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer betNum = null;
		private Integer betGold = null;
		private Integer betCopper = null;

		public RDSGBet(int betNum, int betGold, int betCopper) {
			this.betNum = betNum;
			this.betCopper = betCopper;
			this.betGold = betGold;
		}
	}

}
