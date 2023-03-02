package com.bbw.god.game.combat.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 玩家标识
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 11:21
 */
@Getter
@AllArgsConstructor
public enum PlayerId implements Serializable {
	P1(1), P2(2);
	private int value;

	public static PlayerId fromValue(int value) {
		return P1.value == value ? P1 : P2;
	}
}