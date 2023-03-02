package com.bbw.god.game.config.card;

import com.bbw.god.gameuser.card.UserCard;

import java.math.BigDecimal;

/**
 * 卡牌经验数据工具类
 * 
 * @author suhq
 * @date 2018年11月24日 下午8:06:18
 */
public class CardExpTool {

	/**
	 * 获得卡牌的最大经验
	 * 
	 * @param card
	 * @return
	 */
	public static long getMaxExp(CfgCardEntity card) {
		return CardExpTool.getExpByLevel(card, CardTool.getCardUpdateData().getCardTopLevel()) + 1000;
	}

	/**
	 * 根据经验获得等级
	 * 
	 * @param exper
	 * @return
	 */
//	public static int getUpdatedLevel(CfgCardEntity card, long exp) {
//		long tmpExp = getExpByLevel(card, 11);
//		int cardStar = card.getStar();
//		if (exp < tmpExp) {
//			return getLessTenLevel(exp, getNeedExp(cardStar));
//		}
//		return get13PP(((exp - getTenExp(cardStar)) * 1.0 / getNeedExp(cardStar)[9] + 1) * 0.3 + 1) + 9;
//	}
	
	public static int getUpdatedLevel(CfgCardEntity card, long exp,int nowLevel) {
		long tmpExp = getExpByLevel(card, 11);
		int cardStar = card.getStar();
		if (exp < tmpExp) {
			return getLessTenLevel(exp, getNeedExp(cardStar));
		}
		for (int i = nowLevel+1; i <= 40; i++) {
			long nextExp=getExpByLevel(card, i);
			if (exp < nextExp) {
				return i - 1;
			} else if (exp == nextExp) {
				return i;
			} else if (exp >= nextExp && i == 40) {
				return i;
			}

		}
		//如果超过40级经验或者没匹配 则返回当前等级
		return nowLevel;
	}

	/**
	 * 获得下一级卡牌的经验
	 * 
	 * @return
	 */
	public static long getExpForNextLevel(UserCard userCard) {
		CfgCardEntity card = userCard.gainCard();
		int cardStar = card.getStar();
		int level = userCard.getLevel();
		long experience = userCard.getExperience();
		int tmp = 0;
		if (userCard.getLevel() < 10) {
			return experience + getNeedExp(cardStar)[level];
		}
		tmp = getNeedExp(cardStar)[9];
		return experience + (int) (tmp * (Math.pow(1.3, level - 10)));
	}

	/**
	 * 得到<=10级的等级
	 * 
	 * @param exp
	 * @param needExps
	 * @return
	 */
	private static int getLessTenLevel(long exp, int[] needExps) {
		int levelExp = 0;
		for (int i = 0; i < 10; i++) {
			levelExp += needExps[i];
			if (exp < levelExp) {
				return i;
			}
		}
		return 10;
	}

	/**
	 * 获得卡牌升到level需要的总经验
	 * 
	 * @param level
	 * @return
	 */
	public static long getExpByLevel(CfgCardEntity card, int level) {
		int cardStar = card.getStar();
		long tmp = 0;
		if (level <= 10) {// 检测是否小于等于10
			for (int i = 0; i < level; i++) {
				tmp += getNeedExp(cardStar)[i];
			}
		} else {
			tmp += getTenExp(cardStar);
			tmp += getNeedExp(cardStar)[9] * ((Math.pow(1.3, level - 9) - 1) * 10 / 3 - 1);
		}
		return tmp;
	}

	/**
	 * 玩家从level-1升到level所需要的经验
	 * 
	 * @param newLevel newLevel>=1
	 * @return
	 */
	public static int getNeededExpByLevel(CfgCardEntity card, int newLevel) {
		int cardStar = card.getStar();
		int expTmp = 0;
		if (newLevel < 11) {
			return getNeedExp(cardStar)[newLevel - 1];
		}
		expTmp = getNeedExp(cardStar)[9];
		return (int) (expTmp * Math.pow(1.3, newLevel - 10));
	}

	/**
	 * 得到<=no的1.3的最大幂次方,当no为0时返回-1；
	 * 
	 * @param no
	 * @return
	 */
	private static int get13PP(double no) {
		if (no == 0) {
			return -1;
		}
		int tmp = 0;
		BigDecimal decimal1 = new BigDecimal(Double.toString(no));
		BigDecimal decimal2 = new BigDecimal(Double.toString(1.3));
		while ((decimal1 = decimal1.divide(decimal2, 4)).doubleValue() >= 1) {
			tmp++;
		}
		return tmp;
	}

	private static int[] getNeedExp(int star) {
		switch (star) {
		case 1:
			return CardTool.getCardUpdateData().getNeedExp1();
		case 2:
			return CardTool.getCardUpdateData().getNeedExp2();
		case 3:
			return CardTool.getCardUpdateData().getNeedExp3();
		case 4:
			return CardTool.getCardUpdateData().getNeedExp4();
		case 5:
			return CardTool.getCardUpdateData().getNeedExp5();
		default:
			return CardTool.getCardUpdateData().getNeedExp5();
		}

	}

	private static int getTenExp(int star) {
		switch (star) {
		case 1:
			return CardTool.getCardUpdateData().getTenExp1();
		case 2:
			return CardTool.getCardUpdateData().getTenExp2();
		case 3:
			return CardTool.getCardUpdateData().getTenExp3();
		case 4:
			return CardTool.getCardUpdateData().getTenExp4();
		case 5:
			return CardTool.getCardUpdateData().getTenExp5();
		default:
			return CardTool.getCardUpdateData().getTenExp1();
		}

	}
}
