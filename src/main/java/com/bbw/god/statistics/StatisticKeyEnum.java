package com.bbw.god.statistics;

import lombok.Getter;

/**
 * 统计key
 * 
 * @author suhq
 * @date 2019年3月7日 上午10:38:22
 */
@Getter
public enum StatisticKeyEnum {
	FIRST_CC5("firstCC5", "首个攻下五级城"),
	FIRST_CC4("firstCC4", "首个攻下四级城");
	private String key;
	private String des;

	private StatisticKeyEnum(String key, String des) {
		this.key = key;
		this.des = des;
	}
}
