package com.bbw.god.game.combat.data.card;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 坟场、手牌、牌堆、阵位
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:32
 */
@Getter
@AllArgsConstructor
public enum PositionType implements Serializable {
	DISCARD(0, "坟场"),
	HAND(1, "手牌"),
	DRAWCARD(2, "牌堆"), 
	BATTLE(3, "阵位"), 
	REINFORCEMENTS(4, "援军"),
	DEGENERATOR(5,"异次元");
	private int value;
	private String name;
}
