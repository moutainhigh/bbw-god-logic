package com.bbw.god.gameuser;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 所有的引用cfg基础类的继承该类
 * 
 * @author suhq 2018年10月8日 下午1:57:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class UserCfgObj extends UserData {
	private Integer baseId;//配置ID
	private String name; //名称
}
