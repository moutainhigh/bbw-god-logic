package com.bbw.god.gameuser.res.ele;

import com.bbw.common.PowerRandom;

import lombok.Getter;
import lombok.Setter;

/**
 * 元素传值对象
 * 
 * @author suhq
 * @date 2019-10-18 14:13:14
 */
@Getter
@Setter
public class EVEle {
	private int type;// [10,20,30,40,50]
	private int num;

	public EVEle(int type, int num) {
		this.type = type;
		this.num = num;
	}

	public EVEle(int num) {
		this.type = getRandomEle();
		this.num = num;
	}

	private int getRandomEle() {
		return PowerRandom.getRandomBySeed(5) * 10;
	}
}
