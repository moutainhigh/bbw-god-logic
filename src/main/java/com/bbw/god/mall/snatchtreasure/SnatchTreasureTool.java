package com.bbw.god.mall.snatchtreasure;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝工具
 * @date 2020/6/29 17:27
 **/
public class SnatchTreasureTool {
	public static CfgSnatchTreasureAwards getSnatchTreasureAwards() {
		return Cfg.I.getUniqueConfig(CfgSnatchTreasureAwards.class);
	}

	public static List<CfgSnatchTreasureBox> getSnatchTreasureBoxes() {
		return Cfg.I.get(CfgSnatchTreasureBox.class);
	}

	public static CfgSnatchTreasureBox getSnatchTreasureBox(int boxId) {
		CfgSnatchTreasureBox box = Cfg.I.get(boxId, CfgSnatchTreasureBox.class);
		if (null == box) {
			throw CoderException.high("夺宝周累计箱子id=" + boxId + "不存在");
		}
		return box;
	}

	public static CfgSnatchTreasureCard getSnatchTreasureCards() {
		return Cfg.I.getUniqueConfig(CfgSnatchTreasureCard.class);
	}
}
