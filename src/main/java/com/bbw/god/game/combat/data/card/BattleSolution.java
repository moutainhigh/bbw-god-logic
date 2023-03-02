package com.bbw.god.game.combat.data.card;

import lombok.Data;

/**
 * 上阵方案
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-07 16:57
 */
@Data
public class BattleSolution {
	private BattleSolutionSelector selector;//选择器
	private int[] solution;//上阵的卡牌在手牌的下标索引

	public BattleSolution(BattleSolutionSelector selector, int[] s) {
		//this.emptyBattlePosCount = emptyBattlePosCount;
		//this.solutionCardNum = cardNum;
		this.solution = s;
		this.selector = selector;
	}
}