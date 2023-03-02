package com.bbw.god.gameuser.chamberofcommerce.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 购买商会礼包事件
 *
 * @author suchaobin
 * @date 2020-02-21
 */
@Getter
@Setter
@ToString
public class EPBuyCocBag extends BaseEventParam{
	private Integer bagId;

	public static EPBuyCocBag instance(BaseEventParam ep, int bagId) {
		EPBuyCocBag ev = new EPBuyCocBag();
		ev.setValues(ep);
		ev.setBagId(bagId);
		return ev;
	}
}
