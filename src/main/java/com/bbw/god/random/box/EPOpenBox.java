package com.bbw.god.random.box;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 开启宝箱事件参数
 * @date 2020/2/21 9:34
 */
@Data
public class EPOpenBox extends BaseEventParam {
	private int boxId;
	private int score;

	public EPOpenBox(int boxId, int score, BaseEventParam bep) {
		this.boxId = boxId;
		this.score = score;
		setValues(bep);
	}
}
