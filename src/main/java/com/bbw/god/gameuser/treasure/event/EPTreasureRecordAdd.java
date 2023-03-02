package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 增加法宝记录事件参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureRecordAdd extends BaseEventParam {
	private Integer treasureId;

	public EPTreasureRecordAdd(BaseEventParam bep, Integer treasureId) {
		setValues(bep);
		this.treasureId = treasureId;
	}
}
