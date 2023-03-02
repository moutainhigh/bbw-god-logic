package com.bbw.common;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月3日 下午4:15:11
 */
public class PowerRandom {
	private static char chars[] = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final char INT_CHARS[] = "1234567890".toCharArray();

	/**
	 * 枚举类随机
	 * 
	 * @param enumClass
	 * @return
	 */
	public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
		T[] values = enumClass.getEnumConstants();
		return values[randomInt(values.length)];
	}

	/**
	 * 随机生成length位数字字符串
	 * 
	 * @param length
	 * @return
	 */
	public static final String getRandomLengthDigit(int length) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			str.append(INT_CHARS[(int) (Math.random() * 10)]);
		}
		return str.toString();
	}

	/**
	 * 随即生成length位字符串
	 * 
	 * @param length
	 * @return
	 */
	public static final String getRandomLengthString(int length) {
		if (length < 1) {
			return null;
		}
		char ac[] = new char[length];
		for (int j = 0; j < ac.length; j++) {
			ac[j] = chars[randomInt(71)];
		}

		return new String(ac);
	}

	/**
	 * 随即生成i位中文
	 * 
	 * @param i
	 * @return
	 */
	public static final String getRandomLengthChineseString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = length; i > 0; i--) {
			sb.append(getRandomChinese());
		}
		return sb.toString();
	}

	/**
	 * 随机产生中文,长度范围为start-end
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getRandomLengthChiness(int start, int end) {
		StringBuilder sb = new StringBuilder();
		int length = randomInt(end + 1);
		if (length < start) {
			return getRandomLengthChiness(start, end);
		} else {
			for (int i = 0; i < length; i++) {
				sb.append(getRandomChinese());
			}
		}
		return sb.toString();
	}

	/**
	 * 随机获得中文
	 * 
	 * @return
	 */
	public static String getRandomChinese() {
		String str = null;
		int highPos, lowPos;
		;
		highPos = (176 + Math.abs(randomInt(39)));
		lowPos = 161 + Math.abs(randomInt(93));
		byte[] b = new byte[2];
		b[0] = (new Integer(highPos)).byteValue();
		b[1] = (new Integer(lowPos)).byteValue();
		try {
			str = new String(b, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 生成一个 1 到 seed的随机数 [1,seed]
	 * 
	 * @param seed
	 * @return
	 */
	public static int getRandomBySeed(int seed) {
		return randomInt(seed) + 1;
	}

	/**
	 * 根据概率生成索引
	 * 
	 * @param probs 概率
	 * @param seed 概率和
	 * @return
	 */
	public static int getIndexByProbs(List<Integer> probs, int seed) {
		int random = getRandomBySeed(seed);
		int sum = 0;
		for (int i = 0; i < probs.size(); i++) {
			sum += probs.get(i);
			if (sum >= random) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 根据概率生成索引
	 *
	 * @param probs 概率
	 * @param seed  概率和
	 * @return
	 */
	public static List<Integer> getIndexsByProbs(List<Integer> probs, int seed, int num) {
		List<Integer> results = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			int indexByProbs = -1;
			do {
				indexByProbs = PowerRandom.getIndexByProbs(probs, 10000);
			} while (results.contains(indexByProbs));
			results.add(indexByProbs);
		}
		return results;
	}

	/**
	 * 生成一个 0 到 length-1 的随机数
	 *
	 * @param length
	 * @return
	 */
	private static int getRandomIndexForList(int size) {
		return randomInt(size);
	}

	/**
	 * 得到[min,max]间的随机整数，包括min,max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomBetween(int min, int max) {
		double random = Math.random();
		if (random == 0) {
			return min;
		}
		return min + getRandomBySeed(max - min + 1) - 1;
	}

	/**
	 * 得到min~max间的随机数，包括[min-1,max-1]
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomBetweenForList(int min, int max) {
		double random = Math.random();
		if (random == 0) {
			return min;
		}
		return min + getRandomBySeed(max - min + 1) - 1 - 1;
	}

	/**
	 * 生成一个 1 到 seed的随机数 ，此随机数不包含在要扣除的数组exclude[i]里
	 * 
	 * @param seed
	 * @return
	 */
	public static int getRandomBySeedExclude(int seed, int[] exclude) {
		while (true) {
			int rand = (int) (Math.random() * seed + 1);
			boolean flag = false; // 在要扣除的数组里

			for (int i = 0; i < exclude.length; i++) {
				if (exclude[i] == rand) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				return rand;
			}
		}
	}

	/**
	 * [1 - seed]间取maxSize个不重复的随机整数
	 * 
	 * @param seed
	 * @param maxSize
	 * @return
	 */
	public static List<Integer> getRandomInts(int seed, int maxSize) {
		return Stream.generate(() -> getRandomBySeed(seed)).distinct().limit(maxSize).collect(Collectors.toList());
	}

	/**
	 * [1 - seed]间取maxSize个不重复且不包含在exclude的随机整数
	 * 
	 * @param seed
	 * @param maxSize
	 * @return
	 */
	public static List<Integer> getRandomInts(int seed, int maxSize, int[] exclude) {
		return Stream.generate(() -> getRandomBySeedExclude(seed, exclude)).distinct().limit(maxSize).collect(Collectors.toList());
	}

	/**
	 * [0 - seed-1]间取maxSize个不重复的随机整数
	 * 
	 * @param seed
	 * @param maxSize
	 * @return
	 */
	public static List<Integer> getRandomIndexsForList(int seed, int maxSize) {
		return Stream.generate(() -> getRandomIndexForList(seed)).distinct().limit(maxSize).collect(Collectors.toList());
	}

	/**
	 * 从集合中获取一个随机值
	 *
	 * @param values
	 * @return
	 */
	public static <T> T getRandomFromList(List<T> values) {
		int index = getRandomIndexForList(values.size());
		return values.get(index);
	}

	/**
	 * 从集合中获取一个随机值
	 *
	 * @param values
	 * @param exclude
	 * @param <T>
	 * @return
	 */
	public static <T> T getRandomFromList(List<T> values, List<T> exclude) {
		T value = null;
		do {
			int index = getRandomIndexForList(values.size());
			value = values.get(index);
		} while (exclude.contains(value));
		return value;
	}

	/**
	 * 从集合中获取指定个数的不重复的随机值
	 * <br><font color="red">注意：数量不能小于0，且确保集合有足够数量的项 </font>
	 *
	 * @param values
	 * @param num
	 * @return
	 */
	public static <T> List<T> getRandomsFromList(List<T> values, int num) {
		//目标数量超过源数据，返回源数据的浅副本
		if (num >= values.size()) {
			return values.stream().collect(Collectors.toList());
		}

		List<T> result = new ArrayList<>();
		//源数据为空或者目标数为0
		if (num == 0 || values.isEmpty()) {
			return result;
		}
		//随机num个元素
		List<Integer> excludeIndex = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			int randomIndex = 0;
			do {
				randomIndex = getRandomIndexForList(values.size());
			} while (excludeIndex.contains(randomIndex));
			excludeIndex.add(randomIndex);
			result.add(values.get(randomIndex));
		}
		return result;
	}

	/**
	 * 从集合中获取多个不重复的随机值
	 * <br><font color="red"> 当集合大小比maxNum小时，返回集合全部项 </font>
	 * <br><font color="red">注意：请确保maxNum不小于0 </font>
	 * @param maxNum
	 * @param values
	 * @return
	 */
	public static <T> List<T> getRandomsFromList(int maxNum,List<T> values) {
		return getRandomsFromList(values, maxNum);
	}

	public static <T> Set<T> getRandomsFromSet(Set<T> values, int num) {
		if (values.isEmpty()) {
			return new HashSet<>();
		}
		List<T> list = new ArrayList<>(values);
		List<T> randoms = getRandomsFromList(list, num);
		return new HashSet<>(randoms);
	}

	/**
	 * 从数组中获取一个随机值
	 *
	 * @param values
	 * @return
	 */
	public static <T> T getRandomFromArray(T[] values) {
		return values[getRandomIndexForList(values.length)];
	}

	/**
	 * 获得集合的随机元素
	 * 
	 * @param set
	 * @return
	 */
	public static <T> T getRandomFromSet(Set<T> set) {
		return getRandomFromList(set.stream().collect(Collectors.toList()));
	}

	/**
	 * 洗牌
	 */
	public static void shuffle(List<?> values) {
		int times = PowerRandom.getRandomBySeed(values.size());
		for (int i = 0; i < times; i++) {
			Collections.shuffle(values);
		}
	}
	/**
	 * 产生[0,bound)的随机整数
	 * 
	 * @param bound
	 * @return
	 */
	public static int randomInt(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	/**
	 * 命中概率。以100为100%命中。
	 * @param probability:概率[1,100],int 类型
	 * @return
	 */
	public static boolean hitProbability(int probability) {
		int value = ThreadLocalRandom.current().nextInt(100);
		return value < probability;
	}

	/**
	 * 命中概率。
	 * @param probability 比例
	 * @param count 总数
	 * @return
	 */
	public static boolean hitProbability(int probability,int count) {
		int value = ThreadLocalRandom.current().nextInt(count);
		return value < probability;
	}

	/**
	 * 提供所有概率，返回命中的概率所在的数组下标
	 *
	 * @param probabilities
	 * @return
	 */
	public static int hitProbabilityIndex(List<Integer> probabilities){
		int total=probabilities.stream().collect(Collectors.summingInt(Integer::intValue));
		int seed = getRandomBySeed(total);
		int sum=0;
		for (int i = 0; i < probabilities.size(); i++) {
			sum+=probabilities.get(i);
			if (sum>=seed){
				return i;
			}
		}
		return 0;
	}

}
