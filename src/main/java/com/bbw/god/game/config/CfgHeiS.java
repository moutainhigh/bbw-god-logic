package com.bbw.god.game.config;

import java.util.List;

import lombok.Data;

/**
 * 黑市可产出的法宝
 * 
 * @author suhq
 * @date 2019-05-06 21:23:59
 */
@Data
public class CfgHeiS implements CfgInterface {
	private String key;
	private Integer num;// 黑市一次产出多少个
	private List<HeiSTreasure> treasures;// 法宝列表

	@Data
	public static class HeiSTreasure {
		// 法宝ID
		private Integer id;
		// 法宝名称
		private String name;
		// 价格
		private Integer price;
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
