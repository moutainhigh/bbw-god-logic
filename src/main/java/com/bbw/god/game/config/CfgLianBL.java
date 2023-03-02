package com.bbw.god.game.config;

import java.util.List;

import lombok.Data;

/**
 * 炼宝炉可产出的法宝
 * 
 * @author suhq
 * @date 2019-05-06 21:27:32
 */
@Data
public class CfgLianBL implements CfgInterface {
	private String key;
	private List<LianBLTreasure> treasures;// 任意等级可产出的法宝列表
	private List<LianBLTreasure> highLevelTreasures;// 六级后可产出的法宝列表
	private List<LianBLTreasure> lingshis;// 六级后可产出的灵石列表

	@Data
	public static class LianBLTreasure {
		// 法宝ID
		private Integer id;
		// 法宝名称
		private String name;
		// 星级
		private Integer star;
	}

	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

}
