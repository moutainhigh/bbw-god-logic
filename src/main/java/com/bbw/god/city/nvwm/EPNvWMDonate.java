package com.bbw.god.city.nvwm;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 女娲庙捐赠事件参数
 * @date 2020/2/24 10:39
 */
@Data
public class EPNvWMDonate extends BaseEventParam {
	/**本次女娲庙捐赠获得的好感度*/
	private Integer satisfaction;

	public EPNvWMDonate(Integer satisfaction, BaseEventParam bep) {
		this.satisfaction = satisfaction;
		setValues(bep);
	}
}
