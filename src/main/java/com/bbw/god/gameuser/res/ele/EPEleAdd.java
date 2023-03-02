package com.bbw.god.gameuser.res.ele;

import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获得元素事件参数
 * 
 * @author suhq
 * @date 2019-10-18 14:11:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPEleAdd extends BaseEventParam {
	private List<EVEle> addEles;

	public EPEleAdd(BaseEventParam bep, List<EVEle> addEles) {
		setValues(bep);
		this.addEles = addEles;
	}
}
