package com.bbw.god.mall.lottery;

import com.bbw.common.PowerRandom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 奖券结果
 * @date 2020/7/6 14:44
 **/
@Data
@AllArgsConstructor
public class LotteryResult {
	private List<Integer> firstPrizeNumbers;
	private List<Integer> secondPrizeNumbers;
	private List<Integer> thirdPrizeNumbers;
	private List<Integer> fourthPrizeNumbers;
	private List<Integer> fifthPrizeNumbers;
	private List<Integer> participatePrizeNumbers;

	public static LotteryResult getInstance(List<Integer> boughtNumbers) {
		List<Integer> allNumbers = getAllNumbers();
		// 一等奖
		if (boughtNumbers.size() < 26) {
			allNumbers.removeAll(boughtNumbers);
		}
		List<Integer> first = PowerRandom.getRandomsFromList(allNumbers, 1);
		allNumbers.removeAll(first);
		// 二等奖
		if (boughtNumbers.size() < 17) {
			allNumbers.removeAll(boughtNumbers);
		}
		List<Integer> second = PowerRandom.getRandomsFromList(allNumbers, 1);
		allNumbers = getAllNumbers();
		allNumbers.removeAll(first);
		allNumbers.removeAll(second);
		// 三等奖
		List<Integer> third = PowerRandom.getRandomsFromList(allNumbers, 6);
		allNumbers.removeAll(third);
		// 四等奖
		List<Integer> fourth = PowerRandom.getRandomsFromList(allNumbers, 9);
		allNumbers.removeAll(fourth);
		// 五等奖
		List<Integer> fifth = PowerRandom.getRandomsFromList(allNumbers, 9);
		allNumbers.removeAll(fifth);
		// 参与奖
		List<Integer> participate = PowerRandom.getRandomsFromList(allNumbers, 10);
		allNumbers.removeAll(participate);
		return new LotteryResult(first, second, third, fourth, fifth, participate);
	}

	/**
	 * 获取所有的号码
	 *
	 * @return
	 */
	private static List<Integer> getAllNumbers() {
		List<Integer> allNumbers = new ArrayList<>();
		for (int i = 1; i <= 36; i++) {
			allNumbers.add(i);
		}
		return allNumbers;
	}

	/**
	 * 根据号码获取对应获奖等级
	 *
	 * @param number
	 * @return
	 */
	public LotteryLevel getAwardLevel(int number) {
		if (this.firstPrizeNumbers.contains(number)) {
			return LotteryLevel.FIRST;
		}
		if (this.secondPrizeNumbers.contains(number)) {
			return LotteryLevel.SECOND;
		}
		if (this.thirdPrizeNumbers.contains(number)) {
			return LotteryLevel.THIRD;
		}
		if (this.fourthPrizeNumbers.contains(number)) {
			return LotteryLevel.FOURTH;
		}
		if (this.fifthPrizeNumbers.contains(number)) {
			return LotteryLevel.FIFTH;
		}
		return LotteryLevel.PARTICIPATE;
	}
}
