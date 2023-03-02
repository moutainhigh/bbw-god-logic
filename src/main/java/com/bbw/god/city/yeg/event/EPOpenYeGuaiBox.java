package com.bbw.god.city.yeg.event;

import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 开启野怪宝箱事件参数
 * @date 2020/5/9 11:15
 **/
@Data
public class EPOpenYeGuaiBox extends BaseEventParam {
	private YeGuaiEnum yeGuaiEnum;

	public EPOpenYeGuaiBox(YeGuaiEnum yeGuaiEnum, BaseEventParam bep) {
		this.yeGuaiEnum = yeGuaiEnum;
		setValues(bep);
	}
}
