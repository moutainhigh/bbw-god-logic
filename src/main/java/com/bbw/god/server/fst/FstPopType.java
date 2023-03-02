package com.bbw.god.server.fst;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-12
 */
@Getter
@AllArgsConstructor
public enum FstPopType {
	NONE(0),
	/**
	 * 加入跨服封神台
	 */
	JOIN_TO_GAME_FST(10),
	/**
	 * 开启榜单
	 */
	OPEN_RANKING(20),
	/**
	 * 结算榜单
	 */
	SETTLE_RANKING(30);
	private int type;
}
