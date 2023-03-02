package com.bbw.god.server.flx;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 压压乐元素馆投注参数
 * 
 * @author suhq
 * @date 2018年11月20日 下午5:29:14
 */
@Getter
@Setter
@ToString
public class CPYaYaLeGBet {
	private int bet1; // 元素1
	private int bet2;// 元素2
	private int bet3;// 元素3

	public CPYaYaLeGBet(int bet1, int bet2, int bet3) {
		this.bet1 = bet1;
		this.bet2 = bet2;
		this.bet3 = bet3;
	}

}
