package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 法宝记录重置事件参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureRecordReset extends BaseEventParam {
	private Integer treasureId;

	public EPTreasureRecordReset(BaseEventParam bep, Integer treasureId) {
		setValues(bep);
		this.treasureId = treasureId;
	}
}
