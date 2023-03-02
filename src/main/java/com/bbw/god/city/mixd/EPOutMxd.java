package com.bbw.god.city.mixd;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

import java.util.List;

/**
 * @author suchaobin
 * @description 出迷仙洞事件参数
 * @date 2020/2/24 15:53
 */
@Data
public class EPOutMxd extends BaseEventParam {
	private List<Integer> guAwardPos;

	public EPOutMxd(List<Integer> guAwardPos, BaseEventParam bep) {
		this.guAwardPos = guAwardPos;
		setValues(bep);
	}
}
