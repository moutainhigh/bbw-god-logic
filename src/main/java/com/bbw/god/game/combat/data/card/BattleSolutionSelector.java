package com.bbw.god.game.combat.data.card;

import lombok.Data;

/**
 * 方案有效性的判定条件
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-07 16:57
 */
@Data
public class BattleSolutionSelector {
	private int emptyBattlePosNum;//战场空位数量
	private int emptyYunTaiPosNum;//云台空位数量
	private int maxCardNum;//最多选出多少张卡
	private int mpLimit;//当前玩家拥有的法力值

	private BattleSolutionSelector(int emptyBattlePosNum, int emptyYunTaiPosNum, int maxCardNum, int mpLimit) {
		this.emptyBattlePosNum = emptyBattlePosNum;
		this.emptyYunTaiPosNum = emptyYunTaiPosNum;
		this.maxCardNum = maxCardNum;
		this.mpLimit = mpLimit;
	}

	/**
	 * 没有法力值限制的选择器
	 * @param emptyBattlePosNum:战场空位数量
	 * @param emptyYunTaiPosNum:云台空位数量
	 * @param maxCardNum:最多选出多少张卡
	 * @return
	 */
	public static BattleSolutionSelector getNoMpLimitSelector(int emptyBattlePosNum, int emptyYunTaiPosNum, int maxCardNum) {
		BattleSolutionSelector selector = new BattleSolutionSelector(emptyBattlePosNum, emptyYunTaiPosNum, maxCardNum, Integer.MAX_VALUE);
		return selector;
	}

	/**
	 * 没有法力值限制的选择器
	 * @param emptyBattlePosNum:战场空位数量
	 * @param emptyYunTaiPosNum:云台空位数量
	 * @param maxCardNum:最多选出多少张卡
	 * @param mpLimit:当前玩家拥有的法力值
	 * @return
	 */
	public static BattleSolutionSelector getMpLimitSelector(int emptyBattlePosNum, int emptyYunTaiPosNum, int maxCardNum, int mpLimit) {
		BattleSolutionSelector selector = new BattleSolutionSelector(emptyBattlePosNum, emptyYunTaiPosNum, maxCardNum, mpLimit);
		return selector;
	}
}