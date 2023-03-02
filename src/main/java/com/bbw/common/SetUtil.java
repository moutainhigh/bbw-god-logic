package com.bbw.common;

import java.util.Set;

public class SetUtil {
	/**
	 * 是否为空集合
	 *
	 * @param set
	 * @return
	 */
	public static boolean isNotEmpty(Set<?> set) {
		return set != null && set.size() > 0;
	}

	/**
	 * 是否为空集合
	 *
	 * @param set
	 * @return
	 */
	public static boolean isEmpty(Set<?> set) {
		return set == null || set.size() == 0;
	}
}
