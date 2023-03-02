package com.bbw.god.gameuser.statistic;

import com.bbw.common.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author suchaobin
 * @description 基础统计类
 * @date 2020/3/28 15:28
 */
@Data
@NoArgsConstructor
public abstract class BaseStatistic {
	private Integer today = 0;
	private Integer total = 0;
	private Integer date = DateUtil.getTodayInt();

	public BaseStatistic(Integer date) {
		this.date = date;
	}

	public BaseStatistic(Integer today, Integer total, Integer date) {
		this.today = today;
		this.total = total;
		this.date = date;
	}
}
