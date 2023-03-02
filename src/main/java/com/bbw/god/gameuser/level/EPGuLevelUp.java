package com.bbw.god.gameuser.level;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 玩家等级提升事件
 * 
 * @author suhq
 * @date 2019-10-18 14:49:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPGuLevelUp extends BaseEventParam {
	private Integer oldLevel;
	private Integer newLevel;

	public EPGuLevelUp(BaseEventParam bep, Integer oldLevel, Integer newLevel) {
		setValues(bep);
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
	}
}
