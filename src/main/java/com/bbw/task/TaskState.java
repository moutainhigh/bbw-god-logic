package com.bbw.task;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-08 21:23
 */
public class TaskState {
	/**0 不可接状态*/
	public static final int CAN_NOT_ACCEPT = 0;
	/**1 可接  但还未接的状态*/
	public static final int CAN_ACCEPT = 1;
	/**2 已接  正在进行中*/
	public static final int DOING = 2;
	/**3 完成  未领奖*/
	public static final int COMPLETE = 3;
	/**4 完成  已领奖*/
	public static final int AWARDED = 4;
}
