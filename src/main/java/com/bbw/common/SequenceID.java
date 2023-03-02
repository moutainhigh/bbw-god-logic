package com.bbw.common;

/**
 * 多台服务器使用相同的工作需要
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-17 14:08
 */
public enum SequenceID {
	INSTANCE;
	private static long workerId = 0L;
	private static IdGenerate idGenerate = new IdGenerate(1, workerId);

	/**
	 * 获取机器标识
	 * @return
	 */
	public long getMachineId() {
		return workerId;
	}

	/**
	 * 获得下一个ID (该方法是线程安全的)
	 * @return
	 */
	public long nextId() {
		return idGenerate.nextId();
	}
}
