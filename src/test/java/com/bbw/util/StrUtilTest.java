package com.bbw.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月3日 下午11:18:02
 */
public class StrUtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private boolean getFightAble(int rank, int myRank, int fireRange) {
		if (rank == myRank) {
			return false;
		}

		if (myRank <= fireRange && rank <= fireRange) {
			return true;
		}

		// 竞技场基数
		int step = 2;
		int addFireRange = fireRange * step;
		if (myRank - addFireRange <= rank) {
			return true;
		}
		return false;
	}

	@Test
	public void test() {
		System.out.println(getFightAble(85, 110, 5));
		System.out.println(getFightAble(89, 100, 5));
		System.out.println(getFightAble(90, 100, 5));
		System.out.println(getFightAble(91, 100, 5));
	}
}
