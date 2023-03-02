package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 仙诀参悟
 *
 * @author: huanghb
 * @date: 2022/9/24 11:18
 */
@Data
public class EPXianJueComprehend extends BaseEventParam {
	/** 参悟类别 */
	private Integer comprehendType;
	private Integer fullComprehendNum = 1;

	public static EPXianJueComprehend instance(BaseEventParam ep, int comprehendType) {
		EPXianJueComprehend ew = new EPXianJueComprehend();
		ew.setValues(ep);
		ew.setComprehendType(comprehendType);
		return ew;
	}
}
