package com.bbw.god.statistics.userstatistic;

import com.bbw.common.SpringContextUtil;

/**
 * （临时）
 * 
 * @author suhq
 * @date 2019-08-02 11:30:06
 */
public class ActionStatisticTool {
	private static UserActionStatisticService uActionStatisticService = SpringContextUtil.getBean(UserActionStatisticService.class);

	public static void addUserActionStatistic(long uid, int addNum, String way) {
		uActionStatisticService.add(uid, addNum, way);
	}

	public static int getUserActionStatistic(long uid, String way) {
		return uActionStatisticService.get(uid, way);
	}
}
