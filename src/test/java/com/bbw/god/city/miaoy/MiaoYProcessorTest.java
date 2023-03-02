package com.bbw.god.city.miaoy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.bbw.common.PowerRandom;

public class MiaoYProcessorTest {

	@Test
	public void test() {
		Map<DrawResult, Integer> results = new HashMap<>();
		for (int i = 0; i < 10000; i++) {
			DrawResult result = getDrawLotsResult(5, 50, 30, 15);
			if (results.get(result) == null) {
				results.put(result, 1);
			} else {
				results.put(result, results.get(result) + 1);
			}
		}
		System.out.println(results);
	}

	private DrawResult getDrawLotsResult(int ss, int s, int z, int x) {
		DrawResult result = DrawResult.DOWN;
		int rand = PowerRandom.getRandomBySeed(100);
		if (rand <= ss) {
			result = DrawResult.UP_UP;
		} else if (rand <= ss + s) {
			result = DrawResult.UP;
		} else if (rand <= ss + s + z) {
			result = DrawResult.MIDDLE;
		}
		return result;
	}

}
