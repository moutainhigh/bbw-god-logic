package com.bbw.god.city.yeg;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 野怪类型枚举类
 * @date 2020/1/15 15:43
 */
@Getter
@AllArgsConstructor
public enum YeGuaiEnum {
	YG_NORMAL("普通野怪", 10, 3750), 
	YG_XIONG_SHOU("凶兽来袭", 15, 5000),
	YG_ELITE("精英野怪", 30, 1250),
	YG_FRIEND("好友野怪", 40, 0),
	YG_ELITE_FRIEND("好友精英野怪", 50, 0);
	private String name;
	private int type;
	private int probability;
	public static YeGuaiEnum fromValue(int value) {
		for (YeGuaiEnum item : values()) {
			if (item.getType() == value) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 获取随机的野怪类型
	 *
	 * @return
	 */
	public static YeGuaiEnum randomYeGuai(boolean isInTransmigration) {
		// 100%的概率是10000表示，12.5%则为1250
		int offset = PowerRandom.getRandomBySeed(10000);

		Integer eltiteYgExtraProb = 0;
		if (isInTransmigration) {
			eltiteYgExtraProb = TransmigrationTool.getCfg().getEltiteYgExtraProb();
		}
		List<Integer> probs = new ArrayList<>();
		for (YeGuaiEnum yEnum : values()) {
			int prob = yEnum.getProbability();
			if (yEnum == YG_NORMAL) {
				prob -= eltiteYgExtraProb;
			} else if (yEnum == YG_ELITE) {
				prob += eltiteYgExtraProb;
			}
			probs.add(prob);
		}
		int indexByProbs = PowerRandom.getIndexByProbs(probs, 10000);
		return values()[indexByProbs];
	}

	public static boolean isYeGuaiFriend(YeGuaiEnum yeGuaiEnum) {
		if (yeGuaiEnum == null) {
			return false;
		}
		if (yeGuaiEnum.equals(YG_FRIEND) || yeGuaiEnum.equals(YG_ELITE_FRIEND)) {
			return true;
		}
		return false;
	}
	/**
	 * 是否是特殊的发现野怪
	 * @param yeGuaiEnum
	 * @return
	 */
	public static boolean isSpecialYeGuaiFinder(YeGuaiEnum yeGuaiEnum) {
		if (yeGuaiEnum == null) {
			return false;
		}
		if (yeGuaiEnum.equals(YG_ELITE)) {
			return true;
		}
		return false;
	}
}
