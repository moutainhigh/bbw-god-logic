package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 仙诀淬星
 *
 * @author: huanghb
 * @date: 2022/9/24 11:18
 */
@Data
public class EPXianJueUpdataStar extends BaseEventParam {
	/** 仙诀类型 */
	private Integer xianJueType;
	/** 品质 */
	private Integer quality;

	public static EPXianJueUpdataStar instance(BaseEventParam ep, int xianJueType, int quality) {
		EPXianJueUpdataStar ew = new EPXianJueUpdataStar();
		ew.setValues(ep);
		ew.setXianJueType(xianJueType);
		ew.setQuality(quality);
		return ew;
	}
}
