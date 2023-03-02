package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 仙诀研习
 *
 * @author: huanghb
 * @date: 2022/9/24 11:18
 */
@Data
public class EPXianJueStudy extends BaseEventParam {
	/** 仙诀类型 */
	private Integer xianJueType;
	/**
	 *
	 */
	private Integer fullStudyNum = 1;

	public static EPXianJueStudy instance(BaseEventParam ep, Integer xianJueType) {
		EPXianJueStudy ew = new EPXianJueStudy();
		ew.setValues(ep);
		ew.setXianJueType(xianJueType);
		return ew;
	}
}
