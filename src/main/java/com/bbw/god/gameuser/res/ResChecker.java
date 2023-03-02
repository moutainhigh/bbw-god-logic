package com.bbw.god.gameuser.res;

import com.bbw.exception.CoderException;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.GameUser;

/**
 * 资源检察器
 */
public class ResChecker {

	public static void checkCopper(GameUser gu, long needCopper) {
		if (gu.getCopper() < Math.abs(needCopper)) {
			throw new ExceptionForClientTip("gu.copper.not.enough");
		}
	}

	public static void checkGold(GameUser gu, int needGold) {
		if (gu.getGold() < Math.abs(needGold)) {
			throw new ExceptionForClientTip("gu.gold.not.enough");
		}
	}

	/**
	 * 检测钻石拥有数量是否满足需要的钻石数量
	 *
	 * @param gu
	 * @param needDiamond
	 */
	public static void checkDiamond(GameUser gu, int needDiamond) {
		if (gu.getDiamond() < Math.abs(needDiamond)) {
			throw new ExceptionForClientTip("gu.diamond.not.enough");
		}
	}

	public static void checkDice(GameUser gu, int needDice) {
		if (gu.getDice() < Math.abs(needDice)) {
			throw new ExceptionForClientTip("gu.dice.not.enough");
		}
	}

	public static void checkEle(GameUser gu, int type, int needNum) {
		TypeEnum eleType = TypeEnum.fromValue(type);
		if (null == eleType) {
			throw CoderException.fatal("[" + type + "]是无效的元素类型。");
		}
		needNum=Math.abs(needNum);
		if (eleType == TypeEnum.Gold && gu.getGoldEle() < needNum) {
			throw new ExceptionForClientTip("card.update.not.enough.gold", needNum + "");
		} else if (eleType == TypeEnum.Wood && gu.getWoodEle() < needNum) {
			throw new ExceptionForClientTip("card.update.not.enough.wood", needNum + "");
		} else if (eleType == TypeEnum.Water && gu.getWaterEle() < needNum) {
			throw new ExceptionForClientTip("card.update.not.enough.water", needNum + "");
		} else if (eleType == TypeEnum.Fire && gu.getFireEle() < needNum) {
			throw new ExceptionForClientTip("card.update.not.enough.fire", needNum + "");
		} else if (eleType == TypeEnum.Earth && gu.getEarthEle() < needNum) {
			throw new ExceptionForClientTip("card.update.not.enough.earth", needNum + "");
		}
	}
}
