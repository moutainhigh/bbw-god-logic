package com.bbw.god.server.flx.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 押押乐获得卡牌事件参数
 * @date 2020/2/24 15:09
 */
@Data
public class EPYaYaLeWin extends BaseEventParam {
	private Integer type;

	public EPYaYaLeWin(int type, BaseEventParam bep) {
		this.type = type;
		setValues(bep);
	}
}
