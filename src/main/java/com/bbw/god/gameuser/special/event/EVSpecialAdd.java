package com.bbw.god.gameuser.special.event;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于添加特产事件传值,EV***
 * 
 * @author suhq
 * @date 2018年10月23日 下午6:17:58
 */
@Getter
@Setter
public class EVSpecialAdd {
	private Integer specialId;
	private Integer discount;// 商品折扣，值范围[0,100]。

	public EVSpecialAdd(int spid, int disc) {
		this.specialId = spid;
		this.discount = disc;
	}

	public EVSpecialAdd(int spid) {
		this.specialId = spid;
		this.discount = 100;
	}

	/**
	 * 赠送的特产
	 * 
	 * @param spid
	 * @return
	 */
	public static EVSpecialAdd given(int spid) {
		EVSpecialAdd eva = new EVSpecialAdd(spid, 0);
		return eva;
	}
}
