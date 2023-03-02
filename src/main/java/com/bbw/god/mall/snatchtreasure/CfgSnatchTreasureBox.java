package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝累计宝箱
 * @date 2020/6/29 17:24
 **/
@Data
public class CfgSnatchTreasureBox implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 7603255984351822688L;
	private Integer id;
	private Integer value;
	private List<Award> awards;

	@Override
	public int getSortId() {
		return this.getId();
	}
}
