package com.bbw.common;

import org.junit.Test;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-06 17:34
 */
public class ShareCodeUtilTest {

	@Test
	public void test() {
		long uid = 190416010000005L;
		int size = 50;
		long l = System.currentTimeMillis();
		l = l / 1024 / 256 + uid / 1896561578 + uid / l;
		for (int i = 0; i < size; i++) {
			l += PowerRandom.getRandomBySeed(1024 * 256);
			//System.out.println(l);
			String tokenCode = ShareCodeUtil.toSerialCode(l);
			System.out.println(tokenCode);
		}
	}

}
