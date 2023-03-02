package com.bbw.god.game.combat;

import java.util.ArrayList;
import java.util.List;

import com.bbw.exception.CoderException;

/** 
 * M选N中 N的个数 的解决方法。M个数为从0开始到M-1。N<=M。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-26 23:09
 */
public class SolutionTable {
	public static final int[][] T55 = { { 0, 1, 2, 3, 4 } };
	public static final int[][] T54 = { { 0, 1, 2, 3 }, { 0, 1, 2, 4 }, { 0, 1, 3, 4 }, { 0, 2, 3, 4 }, { 1, 2, 3, 4 } };
	public static final int[][] T53 = { { 0, 1, 2 }, { 0, 1, 3 }, { 0, 2, 3 }, { 1, 2, 3 }, { 0, 1, 4 }, { 0, 2, 4 }, { 1, 2, 4 }, { 0, 3, 4 }, { 1, 3, 4 }, { 2, 3, 4 } };
	public static final int[][] T52 = { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 0, 3 }, { 1, 3 }, { 2, 3 }, { 0, 4 }, { 1, 4 }, { 2, 4 }, { 3, 4 } };
	public static final int[][] T51 = { { 0 }, { 1 }, { 2 }, { 3 }, { 4 } };
	public static final int[][] T44 = { { 0, 1, 2, 3 } };
	public static final int[][] T43 = { { 0, 1, 2 }, { 0, 1, 3 }, { 0, 2, 3 }, { 1, 2, 3 } };
	public static final int[][] T42 = { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 0, 3 }, { 1, 3 }, { 2, 3 } };
	public static final int[][] T41 = { { 0 }, { 1 }, { 2 }, { 3 } };
	public static final int[][] T33 = { { 0, 1, 2 } };
	public static final int[][] T32 = { { 0, 1 }, { 0, 2 }, { 1, 2 } };
	public static final int[][] T31 = { { 0 }, { 1 }, { 2 } };
	public static final int[][] T22 = { { 0, 1 } };
	public static final int[][] T21 = { { 0 }, { 1 } };
	public static final int[][] T11 = { { 0 } };

	/**
	 * M选N中 N的个数 的解决方法。M个数为从0开始到M-1。N<=M。
	 * @param m
	 * @param n
	 * @return
	 */
	public static int[][] getSolutions(int m, int n) {
		int min = Math.min(m, n);
		if (min > m) {
			throw CoderException.high(String.format("参数逻辑错误！必须满足m>=n。当前m=%d,n=%d", m, min));
		}
		if (5 == m && 5 == min) {
			return T55;
		}
		if (5 == m && 4 == min) {
			return T54;
		}
		if (5 == m && 3 == min) {
			return T53;
		}
		if (5 == m && 2 == min) {
			return T52;
		}
		if (5 == m && 1 == min) {
			return T51;
		}
		if (4 == m && 4 == min) {
			return T44;
		}
		if (4 == m && 3 == min) {
			return T43;
		}
		if (4 == m && 2 == min) {
			return T42;
		}
		if (4 == m && 1 == min) {
			return T41;
		}
		if (3 == m && 3 == min) {
			return T33;
		}
		if (3 == m && 2 == min) {
			return T32;
		}
		if (3 == m && 1 == min) {
			return T31;
		}
		if (2 == m && 2 == min) {
			return T22;
		}
		if (2 == m && 1 == min) {
			return T21;
		}
		if (1 == m && 1 == min) {
			return T11;
		}
		return combine(m, min);
	}

	/**
	  *  M选N中 N的个数 的解决方法。M个数为从0开始到M-1。N<=M。
	  * @param m
	  * @param n
	  * @return
	  */
	private static int[][] combine(int m, int n) {
		String[] a = new String[m];
		for (int i = 0; i < a.length; i++) {
			a[i] = String.valueOf(i);
		}
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String[] b = new String[a.length];
		for (int i = 0; i < b.length; i++) {
			if (i < n) {
				b[i] = "1";
			} else
				b[i] = "0";
		}

		int point = 0;
		int nextPoint = 0;
		int count = 0;
		int sum = 0;
		String temp = "1";
		while (true) {
			// 判断是否全部移位完毕
			for (int i = b.length - 1; i >= b.length - n; i--) {
				if (b[i].equals("1"))
					sum += 1;
			}
			// 根据移位生成数据
			for (int i = 0; i < b.length; i++) {
				if (b[i].equals("1")) {
					point = i;
					sb.append(",");
					sb.append(a[point]);
					count++;
					if (count == n)
						break;
				}
			}
			// 往返回值列表添加数据
			list.add(sb.substring(1));

			// 当数组的最后num位全部为1 退出
			if (sum == n) {
				break;
			}
			sum = 0;

			// 修改从左往右第一个10变成01
			for (int i = 0; i < b.length - 1; i++) {
				if (b[i].equals("1") && b[i + 1].equals("0")) {
					point = i;
					nextPoint = i + 1;
					b[point] = "0";
					b[nextPoint] = "1";
					break;
				}
			}
			// 将 i-point个元素的1往前移动 0往后移动
			for (int i = 0; i < point - 1; i++)
				for (int j = i; j < point - 1; j++) {
					if (b[i].equals("0")) {
						temp = b[i];
						b[i] = b[j + 1];
						b[j + 1] = temp;
					}
				}
			// 清空 StringBuffer
			sb.setLength(0);
			count = 0;
		}
		// 
		//System.out.println("解决方案数量:" + list.size());
		int[][] solutions = new int[list.size()][n];
		for (int i = 0; i < list.size(); i++) {
			String[] solution = list.get(i).split(",");
			for (int j = 0; j < solution.length; j++) {
				solutions[i][j] = Integer.parseInt(solution[j]);
			}
		}
		return solutions;
	}
}
