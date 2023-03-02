package com.bbw.god.game.combat.deploy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bbw.common.JSONUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;

import lombok.extern.slf4j.Slf4j;

/**
 * 攻击力最大
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-14 21:59
 */
@Slf4j
@Service
public class MaxAtkStrategy implements DeployCardsStrategy {

	@Override
	public int[] getSolution(Combat combat, Player player, List<int[]> solutions, int[] freeBattlePos) {
		List<BattleCard> playingCards=combat.getPlayingCards(player.getId(), true);
		boolean f = combat.getFightType().equals(FightTypeEnum.TRAINING)
				|| combat.getFightType().equals(FightTypeEnum.HELP_YG)
				|| combat.getFightType().equals(FightTypeEnum.YG);
		if (f && player.getUid()<0) {
			//攻城、进阶 AI 采用优先组合技
			try {
				//过滤并获取卡牌数量最多的组合技  上牌策略集合索引
				List<Integer> groupSkillIndexs=GroupSkillFilter.getBestGroup(solutions, player.getHandCards(), playingCards);
				if (groupSkillIndexs.size()>0) {
					 List<int[]> groupSkillsolutions=new ArrayList<int[]>();
					for (Integer index:groupSkillIndexs) {
						groupSkillsolutions.add(solutions.get(index));
					}
					//获取组合技上牌策略集合中 攻击力最大的策略
					return maxAtk(groupSkillsolutions, player.getHandCards(), freeBattlePos);
				}
			} catch (Exception e) {
				log.error("组合技筛选异常："+e.getMessage());
			}
		}
		return maxAtk(solutions, player.getHandCards(), freeBattlePos);
	}

	/**
	 * 最大攻击力
	 * @param player
	 * @param solutions
	 * @return
	 */
	static int[] maxAtk(List<int[]> solutions, final BattleCard[] handCards, int[] freeBattlePos) {
		//查找最大攻击力的解决方案
		int index = 0;
		int max = 0;
		for (int i = 0; i < solutions.size(); i++) {
			int atk = 0;
			for (int j = 0; j < solutions.get(i).length; j++) {
				if (null == handCards[solutions.get(i)[j]]) {
					System.out.println(JSONUtil.toJson(solutions.get(i)));
					System.out.println("i=" + i + " j=" + j);
				}
				atk += handCards[solutions.get(i)[j]].getAtk();
			}
			if (atk > max) {
				index = i;
				max = atk;
			}
		}
		int[] solution = solutions.get(index);
		return solution;
	}
}
