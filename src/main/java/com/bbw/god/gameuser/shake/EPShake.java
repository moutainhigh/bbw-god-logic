package com.bbw.god.gameuser.shake;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

import java.util.List;

/**
 * @author suchaobin
 * @description 丢骰子事件参数
 * @date 2020/2/24 10:55
 */
@Data
public class EPShake extends BaseEventParam {
	/**骰子点数集合*/
	private List<Integer> shakeList;

	public EPShake(List<Integer> shakeList, BaseEventParam bep) {
		this.shakeList = shakeList;
		setValues(bep);
	}
}
