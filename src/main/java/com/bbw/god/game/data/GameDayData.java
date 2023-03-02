package com.bbw.god.game.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 所有的区服数据集成该类
 * 
 * @author suhq
 * @date 2018年11月28日 下午2:40:59
 */
@Getter
@Setter
public abstract class GameDayData extends GameData {
	protected int dateInt;//日期yyyyMMdd
}
