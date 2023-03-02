package com.bbw.common;

import java.util.Map;

/**
 * Map工具类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.0.0
 */
public class MapUtil {
	public static boolean isEmpty(Map map) {
		return null == map || map.size() == 0;
	}

	public static boolean isNotEmpty(Map map) {
		return null != map && map.size() != 0;
	}
}
