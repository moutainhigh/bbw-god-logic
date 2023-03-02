package com.bbw.god.game.data;

import org.junit.Test;

import com.bbw.god.game.flx.FlxDayResult;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-20 08:10
 */
public class GameDataDescComparatorTest {

	@Test
	public void test() {
		FlxDayResult[] datas = { new FlxDayResult(), new FlxDayResult() };
		datas[0].setDateInt(2019);
		datas[1].setDateInt(2018);
		java.util.Arrays.sort(datas, new GameDataDescByDateComparator());
		for (FlxDayResult d : datas) {
			System.out.println(d.dateInt);
		}
	}

}
