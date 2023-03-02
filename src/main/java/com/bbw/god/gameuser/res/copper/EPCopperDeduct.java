package com.bbw.god.gameuser.res.copper;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 铜钱扣除信息
 *
 * @author suhq
 * @date 2019-10-16 14:45:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCopperDeduct extends BaseEventParam {
	private long deductCopper;

	public EPCopperDeduct(BaseEventParam baseEP, long deductCopper) {
		setValues(baseEP);
		this.deductCopper = deductCopper;
	}

}
