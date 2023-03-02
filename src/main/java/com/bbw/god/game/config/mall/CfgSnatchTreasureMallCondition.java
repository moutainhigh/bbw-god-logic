package com.bbw.god.game.config.mall;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 夺宝兑换商店配置类
 * @date 2020/6/30 17:09
 **/
@Data
public class CfgSnatchTreasureMallCondition implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = -5114467053972771718L;
	private Integer mallId;
	// 第几周开始展示
	private Integer showWeek;
	// 第几周开始卖
	private Integer sellWeek;

	@Override
	public Serializable getId() {
		return this.mallId;
	}

	@Override
	public int getSortId() {
		return this.getMallId();
	}
}
