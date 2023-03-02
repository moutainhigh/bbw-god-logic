package com.bbw.god.game.combat.data.param;

import lombok.Data;

/**
 * 卡牌移动参数
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:39
 */
@Data
public class CardMovement {
	private int fromPos = 0;//移动前位置
	private int toPos = 0;//移动后位置

	public CardMovement(int from, int to) {
		this.fromPos = from;
		this.toPos = to;
	}
}