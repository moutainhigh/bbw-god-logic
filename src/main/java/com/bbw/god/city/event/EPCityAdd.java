package com.bbw.god.city.event;

import lombok.Data;

/**
 * 攻下城池事件参数
 *
 * @author suhq
 * @date 2019-06-10 15:06:40
 */
@Data
public class EPCityAdd {
	private int cityId;
	private boolean nightmare=false;// 世界类型

	public EPCityAdd(int cityId, boolean nightmare) {
		this.cityId = cityId;
		this.nightmare = nightmare;
	}
}
