package com.bbw.god.gameuser.res.ele;

import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扣除元素事件参数
 * 
 * @author suhq
 * @date 2019-10-18 14:11:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPEleDeduct extends BaseEventParam {
	private List<EVEle> deductEles;

	public EPEleDeduct(BaseEventParam bep, List<EVEle> deductEles) {
		setValues(bep);
		this.deductEles = deductEles;
	}
}
