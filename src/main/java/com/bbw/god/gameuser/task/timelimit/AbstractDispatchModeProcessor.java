package com.bbw.god.gameuser.task.timelimit;

import java.util.Date;

/**
 * 派遣模式处理类
 *
 * @author: suhq
 * @date: 2022/12/9 11:24 上午
 */
public abstract class AbstractDispatchModeProcessor {

	/**
	 * 获取派遣成功率
	 *
	 * @param dispatchTask
	 * @return
	 */
	public abstract int getSuccessRate(UserTimeLimitTask dispatchTask);

	/**
	 * 获取派遣时间
	 *
	 * @param dispatchTask
	 * @return
	 */
	public abstract Date getDispatchDate(UserTimeLimitTask dispatchTask);

	/**
	 * 获得派遣分钟数
	 *
	 * @param dispatchTask
	 * @return
	 */
	public abstract int getDispatchMinute(UserTimeLimitTask dispatchTask);


	/**
	 * 派遣模式匹配
	 *
	 * @param dispatchMode
	 * @return
	 */
	public abstract boolean isMatch(DispatchModeEnum dispatchMode);

}
