package com.bbw.common;

import org.junit.Test;

public class LimitWordsTest {

	@Test
	public void test() {
		System.out.println(LimitWords.isLimit("法轮功"));
		System.out.println(LimitWords.isLimit("voa"));
	}

}
