package com.bbw.java8;

import com.bbw.common.DateUtil;
import com.bbw.common.MathTool;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月28日 上午2:59:40
 */
public class MathTest {

	@Test
	public void test() {
		double[] nums = { 1.4, 1.5, 1.6, -1.4, -1.5, -1.6 };
		for (double num : nums) {
			test(num);
		}
	}

	private static void test(double num) {
		System.out.println("Math.floor(" + num + ")=" + Math.floor(num));
		System.out.println("Math.round(" + num + ")=" + Math.round(num));
		System.out.println("Math.ceil(" + num + ")=" + Math.ceil(num));
		System.out.println("double add:" + MathTool.add(0.1, DateUtil.toDateTimeDouble()));
	}

	@Test
	public void testDecimals() {
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.001), BigDecimal.valueOf(0.001)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.001), BigDecimal.valueOf(0.0011)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.0011), BigDecimal.valueOf(0.001)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.00100000), BigDecimal.valueOf(0.001)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.001), BigDecimal.valueOf(0.00100000)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.00000001), BigDecimal.valueOf(0.00000001)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.00100000), BigDecimal.valueOf(0.00100001)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.00100001), BigDecimal.valueOf(0.00100000)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.000000011), BigDecimal.valueOf(0.000000010)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.000000010), BigDecimal.valueOf(0.000000011)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.001000001), BigDecimal.valueOf(0.0010000001)));
		Assert.assertEquals(true, isEqual(BigDecimal.valueOf(0.001000001), BigDecimal.valueOf(0.00100000010)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.001010001), BigDecimal.valueOf(0.00100010010)));
		Assert.assertEquals(false, isEqual(BigDecimal.valueOf(0.00100010010), BigDecimal.valueOf(0.001010001)));
	}

	/**
	 * 比较两个decimal。按8位精度比较
	 * <p>0.00011比0.00011 -> true</p>
	 * <p>0.00011比0.0001 -> false</p>
	 * <p>0.00000001比0.00000001 -> true</p>
	 * <p>0.00000001比0.00000002 -> false</p>
	 * <p>0.000000011比0.00000001 -> true</p>
	 *
	 * @param decimal1
	 * @param decimal2
	 * @return
	 */
	private boolean isEqual(BigDecimal decimal1, BigDecimal decimal2) {
		BigDecimal result = decimal1.subtract(decimal2);
		result = result.abs();
		return result.compareTo(BigDecimal.valueOf(0.00000001)) < 0;
	}

}
