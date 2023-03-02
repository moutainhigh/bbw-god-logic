package com.bbw.mc;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接收人员
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-12 16:45
 */
@Getter
@AllArgsConstructor
public enum Person implements Serializable {

	Coder("程序员"),
	Manager("管理人员"),
	Operator("运营人员"),
	GameUser("玩家");

	private String name;
}
