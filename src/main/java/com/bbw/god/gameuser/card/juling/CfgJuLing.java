package com.bbw.god.gameuser.card.juling;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

@Data
public class CfgJuLing implements CfgInterface, Serializable {

	private static final long serialVersionUID = 1L;

	private String key;
	// 普通聚灵可选卡牌
	private List<Integer> jlCards;
	// 聚灵指定卡牌需要的元宝
	private int needGoldForJL;
	// 聚灵需要的聚灵旗数量
	private int needJlqForJL;

	// 限定聚灵可选卡牌
	private List<Integer> jlXDCards;
	// 限定聚灵指定卡牌需要的元宝
	private int needGoldForJLXD;
	// 限定聚灵需要的唤神符数量
	private int needHsfForJLXD;
	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

}
