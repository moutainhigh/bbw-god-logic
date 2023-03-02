package com.bbw.god.game.combat.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bbw.god.game.combat.data.card.BattleCard;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月19日 上午11:07:19 
* 类说明 
*/
public class GroupSkillFilter {
	//只需2张即可形成组合技
	private static final int[] twoCards = { 2001, 2002, 2003, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016 };
	//需要最少3张卡才能形成组合技
	private static final int[] threeCards = { 2001, 2002, 2003, 2004, 2005, 2006, 2012, 2014, 2016 };

	/**
	 * 获取形成卡牌数量最多的组合技 上牌策略下标，没有组合技则返回空集合
	 * @param solutions
	 * @param handCards
	 * @param playingCards
	 * @return
	 */
	public static List<Integer> getBestGroup(List<int[]> solutions,BattleCard[] handCards,List<BattleCard> playingCards){
	    Map<Integer, Long> groupSkillMap=playingCards.stream().filter(p->p.getGroupId()>0).collect(Collectors.groupingBy(BattleCard::getGroupId,Collectors.counting()));
		Long max=0L;
		List<Integer> index=new ArrayList<Integer>();
		for (int i = 0; i < solutions.size(); i++) {
			List<BattleCard> cards = new ArrayList<BattleCard>();
			for (int j = 0; j < solutions.get(i).length; j++) {
				BattleCard card = handCards[solutions.get(i)[j]];
				cards.add(card);
			}
			//优先获取需求3张及以上的组合技，将返回最大的组合技 牌数量
			Long num=checkThree(cards, groupSkillMap);
			if (max<3 && num==0) {
				//未发现有需要3张以上的组合技，则检索需求2张的组合技,当已有符合3张的组合技 则不再检索
				num=checkTwo(cards, groupSkillMap);
			}
			//当前数量最大，则清空返回的最优组合下标集合，并加入当前最优的下标
			if (max<num) {
				index=new ArrayList<Integer>();
				index.add(i);
				max=num;
			}else if (max>0 && max==num) {
				//与当前检索到的最优组合 牌数量相同，也算做是最优组合之一
				index.add(i);
			}
		}
		return index;
	}
	
	/**
	 * 筛选 需要3张及以上才能形成组合的组合技
	 * @param cards
	 * @param playingMap
	 * @return
	 */
	private static Long checkThree(List<BattleCard> cards,Map<Integer, Long> playingMap) {
		return check(cards, threeCards, 3,playingMap);
	}
	/**
	 * 筛选 需要2张及以上才能形成组合技的
	 * @param cards
	 * @param playingMap
	 * @return
	 */
	private static Long checkTwo(List<BattleCard> cards,Map<Integer, Long> playingMap) {
		return check(cards, twoCards, 2,playingMap);
	}

	private static Long check(List<BattleCard> cards, int[] groups, int min,Map<Integer, Long> playingMap) {
		if (cards.size() < min) {
			//总卡牌数量少于最小要求则不形成组合
			return 0L;
		}
		Map<Integer, Long> skillMap = cards.stream().filter(p -> p.getGroupId() > 0).collect(Collectors.groupingBy(BattleCard::getGroupId, Collectors.counting()));
		for (int i = 0; i < groups.length; i++) {
			int skill=groups[i];
			Long num=skillMap.get(skill) == null?0L:skillMap.get(skill);
			if (num==0) {
				//手牌中没有当前组合技 则跳过
				continue;
			}
			num += playingMap.get(skill) == null ? 0L : playingMap.get(skill);
			if ( num >= min) {
				return num;
			}
		}
		return 0L;
	}
}
