package com.bbw.validator;

import com.bbw.common.StrUtil;
import com.bbw.exception.GodException;

/**
 * 数据校验
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-02 15:55
 */
public abstract class Assert {

	public static void assertBlank(String str, String message) {
		if (StrUtil.isNull(str)) {
			throw new GodException(message);
		}
	}

	public static void assertNull(Object object, String message) {
		if (object == null) {
			throw new GodException(message);
		}
	}

	public static void assertTrue(boolean b) {
		if (!b) {
			throw new GodException("值必须为TRUE");
		}
	}

	public static void assertFalse(boolean b) {
		if (b) {
			throw new GodException("值必须为FALSE");
		}
	}

}
