package com.bbw.common;

/**
 * 正则表达式帮助
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-16 14:13
 */
public class RegexPatternHelper {
	/**
	 * 将"."和"*"替换成正则表达式的表达方式
	 * @param regex
	 * @return
	 */
	public static String compile(String regex) {
		return regex.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\.*");
	}
}
