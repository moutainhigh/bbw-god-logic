package com.bbw.god.game.combat.deploy;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-14 21:55
 */
@Service
public interface DeployCardsStrategy {
	/**
	 * 根据指定的策略获取一个最佳方案
	 * @param combat
	 * @param player
	 * @param solutions
	 * @return
	 */
	int[] getSolution(Combat combat, Player player, List<int[]> solutions, int[] freeBattlePos);
}
