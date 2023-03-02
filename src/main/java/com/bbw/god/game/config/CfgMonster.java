package com.bbw.god.game.config;

import lombok.Data;

/**
 * 友怪配置
 * 
 * @author suhq
 * @date 2019年3月11日 下午9:53:36
 */
@Data
public class CfgMonster implements CfgInterface {
	private String key;
	/** 系统自动生成的好友的怪物的等级 */
	private int monsterDefaultLevel;
	/** 帮好友打怪冷却时间 秒 */
	private int monsterColdTime;

	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public int getSortId() {
		return 1;
	}
}
