package com.bbw.god.game.config.card;

/**
 * 卡牌升阶工具类
 *
 * @author: suhq
 * @date: 2021/11/9 11:45 上午
 */
public class CardHvTool {

	/**
	 * 升级所需要的灵石
	 *
	 * @param hierarchy
	 * @return
	 */
	public static int getNeededLingshiForUpdate(int hierarchy) {
		int tmp = hierarchy / 2 + 1;
		switch (tmp) {
			case 1:
				return 10;
			case 2:
				return 30;
			case 3:
				return 50;
			case 4:
				return 80;
			case 5:
				return 130;
			default:
				return 130;
		}
	}

	/**
	 * 升阶所需要的混沌仙石
	 *
	 * @param hierarchy
	 * @param star
	 * @return
	 */
	public static int getNeededHYXSForUpdate(int hierarchy, int star) {
		if (hierarchy <= 5) {
			return 0;
		}
		switch (star) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 6;
			case 4:
				return 20;
			case 5:
				return 40;
			default:
				return 40;
		}
	}
}
