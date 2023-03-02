package com.bbw.god.game.data;

import java.util.Comparator;

/**
 * 按照日期大小倒序比较器。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-20 08:06
 */
public class GameDataDescByDateComparator implements Comparator<GameDayData> {
	@Override
	public int compare(GameDayData data1, GameDayData data2) {
		return data2.getDateInt() - data1.getDateInt();
	}

}
