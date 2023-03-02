package com.bbw.god.server;

import java.util.Date;

import com.bbw.common.DateUtil;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-22 09:49
 */
public class ServerDataID {
	/**
	 * 根据区服配置ID生成每日的数据ID
	 * @param sid：
	 * @param date
	 * @param configId
	 * @return
	 */
	public static Long generateConfigID(int sid, Date date, ServerDataType dateType, int configId) {
		String shortDate = DateUtil.toString(date, "MMdd");
		//区服ID+4位日期(MMdd)+2位数据类型+3位配置ID
		String tpl = "%02d%03d";
		String id = sid + shortDate + String.format(tpl, dateType.getTypeId(), configId);
		return Long.valueOf(id);
	}
}
