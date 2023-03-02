package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.random.box.BoxGood;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝奖励的配置类
 * @date 2020/6/29 17:24
 **/
@Data
public class CfgSnatchTreasureAwards implements CfgInterface, Serializable {
	private static final long serialVersionUID = 7603255984351822688L;
	private String key;
	private String desc;// 箱子、礼包描述
	private List<BoxGood> goods;// 物品集


	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 0;
	}
}
