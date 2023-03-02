package com.bbw.common;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @author suchaobin
 * @description bitset工具类
 * @date 2020/6/23 16:44
 **/
public class BitSetUtil {

	/**
	 * 将bitset转成集合
	 *
	 * @param bitSet
	 * @return
	 */
	public static List<Integer> toList(BitSet bitSet) {
		List<Integer> list = new ArrayList<>();
		if (isEmpty(bitSet)) {
			return list;
		}
		String str = bitSet.toString();
		str = str.replace("{", "").replace("}", "").replaceAll(" ", "");
		String[] split = str.split(",");
		for (String s : split) {
			if (StrUtil.isBlank(s)) {
				continue;
			}
			list.add(Integer.parseInt(s));
		}
		return list;
	}

	public static boolean isEmpty(BitSet bitSet) {
		return null == bitSet || 0 == bitSet.size();
	}
}
