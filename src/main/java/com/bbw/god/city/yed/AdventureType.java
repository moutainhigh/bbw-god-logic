package com.bbw.god.city.yed;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 奇遇类型枚举
 * @date 2020/6/9 10:48
 **/
@Getter
@AllArgsConstructor
public enum AdventureType {
	XRSY("仙人授业", 10),
	YYSR("云游商人", 20),
	CUN_ZHUANG_TASK("村庄任务", 30),
	;
	private final String name;
	private final Integer value;
}
