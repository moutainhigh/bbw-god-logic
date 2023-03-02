package com.bbw.god.random.config;

import java.io.Serializable;

import lombok.Data;

/**卡牌选择几率
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 21:29
 */
@Data
public class SelectorProbability implements Serializable {
	private static final long serialVersionUID = 297019076597195962L;
	private int type;//概率类型0|1。0=真随机;1=伪随机。
	private String value;// 概率[0,100]，present:当前概率。 这两个参数仅在伪随机时候有效。
	private transient int addition;//额外概率百分比[-100,100]。 大福神等神仙影响。运行时需要，持久化不需要。避免永远有加成。
	private String maxTimes = String.valueOf(Integer.MAX_VALUE);// 保底策略，最多maxTimes就必须要有命中
	private int loopTimes = 0;//概率重置重新算调用次数

	public double gainValueDouble() {
		return Double.valueOf(value);
	}

	public int gainMaxTimesInt() {
		return Integer.valueOf(maxTimes);
	}

	public boolean needValueParam() {
		if (null == value) {
			return false;
		}
		return value.startsWith(RandomKeys.PARAM_PREFIX);
	}

	public boolean needMaxTimesParam() {
		if (null == maxTimes) {
			return false;
		}
		return maxTimes.startsWith(RandomKeys.PARAM_PREFIX);
	}

	/**
	 * 机器随机。真随机。
	 * @return
	 */
	public boolean isMachineRandom() {
		return (0 == type) || (!needValueParam() && gainValueDouble() >= 100);
	}

	/**
	 * 伪随机。指定type=1，并且概率值<100;
	 * @return
	 */
	public boolean isPRDRandom() {
		return (1 == type) && (needValueParam() || gainValueDouble() < 100);
	}

	/**
	 * 如果是伪随机（指定type=1，并且概率值<100）算法，则增加失败次数。
	 */
	// public void incFailTimes() {
	// if (isPRDRandom()) {
	// loopTimes++;
	// }
	// }

	public void addFailTimes(int addNum) {
		if (isPRDRandom()) {
			loopTimes += addNum;
		}
	}

	// public void reduceFailTimes() {
	// if (loopTimes > 1) {
	// loopTimes--;
	// }
	// }

	/**
	 * 达到保底次数,下次必然获得
	 * 
	 * @return
	 */
	public boolean overMaxtimes() {
		return loopTimes >= gainMaxTimesInt();
	}
}
