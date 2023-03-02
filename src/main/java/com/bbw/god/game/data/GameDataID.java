package com.bbw.god.game.data;

import java.io.Serializable;
import java.util.Date;

import com.bbw.common.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-22 09:49
 */
public class GameDataID {
	/**
	 * 根据区服配置ID生成每日的数据ID
	 * @param sid：
	 * @param date
	 * @param configId
	 * @return
	 */
	public static Long generateDataIdByConfig(Date date, ConfigDataTypeEnum dateType, int configId) {
		String shortDate = DateUtil.toString(date, "YYMMdd");
		//6位日期(YYMMdd)+2位数据类型+2位配置ID
		String tpl = "%02d%02d";
		String id = shortDate + String.format(tpl, dateType.getValue(), configId);
		return Long.valueOf(id);
	}

	@Getter
	@AllArgsConstructor
	public static enum ConfigDataTypeEnum implements Serializable {

		FLX("福临轩", 10);

		private String name;
		private int value;
	}
}
