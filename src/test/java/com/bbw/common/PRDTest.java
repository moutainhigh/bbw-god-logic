package com.bbw.common;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 14:30
 */
public class PRDTest {

	@Test
	public void test() {
		//lookupTable.put(10, 1.474584478107266);
		//maxTimes();
		//timesTest();
		match();
		//getTable();
	}

	public static void maxTimes() {
		for (int i = 1; i < 100; i++) {
			double init = PRDTable.getCFromP((double) i);
			double match = init;
			int times1 = 0;
			do {
				match += init;
				times1++;
			} while (match < 100);
			//////////////////////////////////////////////////////
			if (i > 30) {
				continue;
			}
			int times2 = 0;
			init = 1 - i * 0.01;
			double noMatch = init;
			do {
				noMatch = noMatch * init;
				times2++;
			} while (Math.abs(noMatch) >= 0.00000000001);
			System.out.println("概率" + i + "伪随机最多需要" + times1 + "次,真随机最多需要" + times2 + "次");
		}
	}

	public static double halfUp6(double f) {
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	public static void timesTest() {
		double init1 = 0.99;//初始化不中的概率
		double end = 1;//多次后不中的概率
		int totalTimes = 100;//抽奖次数
		System.out.println("连抽不中代表两次命中之间可能最大的距离");
		for (int i = 0; i < totalTimes; i++) {
			end = end * init1;
		}
		System.out.println("连抽[" + totalTimes + "]次不中的概率是=" + end);
		totalTimes = 300;
		end = 1;
		for (int i = 0; i < totalTimes; i++) {
			end = end * init1;
		}
		System.out.println("连抽[" + totalTimes + "]不中的概率是=" + end);

		double init2 = 0.015604169167720937;
		end = 1 - init2;
		totalTimes = 70;
		for (int times = 0; times < totalTimes; times++) {
			end = end - init2;
		}
		System.out.println("连抽[" + totalTimes + "]不中的概率是=" + end);
	}

	public static void getTable() {
		for (int i = 1; i <= 9; ++i) {
			double p = i / 100.0;
			double C = PRDTable.getCFromP(p);
			//C = halfUp6(C);
			System.out.println(String.format("lookupTable.put(%s, %s);", i, C));
		}
		for (int i = 1; i <= 9; ++i) {
			double p = i / 10.0;
			double C = PRDTable.getCFromP(p);
			//C = halfUp6(C);
			System.out.println(String.format("lookupTable.put(%s, %s);", i * 10, C));
		}
		for (int i = 1; i <= 100; ++i) {
			double p = i / 1.0;
			double C = PRDTable.getCFromP(p);
			//C = halfUp6(C);
			System.out.println(String.format("lookupTable.put(%s, %s);", i * 100, C));
		}
	}

	public static void match() {
		double gailv = 0.5;
		double init = PRDTable.getCFromP(gailv);
		double curr = init;
		int matchTimes = 0;
		int totalTimes = 10000;
		System.out.println("概率：" + gailv + " 运行次数:" + totalTimes);
		for (int times = 0; times < totalTimes; times++) {
			double num = ThreadLocalRandom.current().nextDouble(0, 100);
			if (num < curr) {
				System.out.println("第" + times + "次命中。" + num);
				curr = init;//重置
				matchTimes++;
			} else {
				curr += init;
			}
		}

		System.out.println("伪随机命中次数：" + matchTimes);
		//System.out.println("------------------------------------------------------------------------");
		matchTimes = 0;
		double jia = gailv;
		for (int times = 0; times < totalTimes; times++) {
			double num = ThreadLocalRandom.current().nextDouble(0, 100);
			if (num < jia) {
				System.out.println("第" + times + "次命中。" + num);
				matchTimes++;
			} else {
				curr += init;
			}
		}
		System.out.println("真随机命中次数：" + matchTimes);
	}
}
