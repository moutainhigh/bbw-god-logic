package com.bbw.god.gameuser.res.diamond;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 钻石扣除信息
 *
 * @author: huanghb
 * @date: 2022/6/15 17:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDiamondDeduct extends BaseEventParam {
	private int deductDiamond;

	public EPDiamondDeduct(BaseEventParam baseEP, int deductDiamond) {
		setValues(baseEP);
		this.deductDiamond = deductDiamond;
	}

}
