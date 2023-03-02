package com.bbw.god.gameuser.card;

import com.bbw.exception.ExceptionForClientTip;

import java.util.List;

/**
 * 卡牌逻辑检察器
 *
 * @author suhq
 * @date 2018年11月24日 下午8:20:58
 */
public class CardChecker {

	/**
	 * 卡牌拥有检查
	 * 
	 * @param userCard
	 */
	public static void checkIsOwn(UserCard userCard) {
		if (userCard == null) {
			throw new ExceptionForClientTip("card.not.own");
		}
	}

	/**
	 * 卡牌是否升满检查
	 * 
	 * @param userCard
	 */
	public static void checkIsFullUpdate(UserCard userCard) {
		if (userCard.ifFullUpdate()) {
			throw new ExceptionForClientTip("card.update.already.full");
		}
	}

	/**
	 * 卡牌是否满阶检查
	 *
	 * @param userCard
	 */
	public static void checkIsFullHierarchy(UserCard userCard) {
		if (userCard.ifFullHierarchy()) {
			throw new ExceptionForClientTip("card.hierarchy.already.full");
		}
	}


	/**
	 * 检查卡牌集合是否都与type同属性
	 *
	 * @param type
	 * @param cardIds
	 * @return
	 */
	public static boolean isSameType(int type, List<Integer> cardIds) {
		//相克属性
		int counterattackType = 0;
		switch (type) {
			case 10:
				counterattackType = 4;
				break;
			case 20:
				counterattackType = 1;
				break;
			case 30:
				counterattackType = 5;
				break;
			case 40:
				counterattackType = 3;
				break;
			case 50:
				counterattackType = 2;
				break;
			default:
		}
		for (Integer id : cardIds) {
			int cardId = id;
			if (id > 10000) {
				cardId = id - 10000;
			}
			if (cardId / 100 == counterattackType) {
				//同属性
				return false;
			}
		}
		return true;
	}
}
