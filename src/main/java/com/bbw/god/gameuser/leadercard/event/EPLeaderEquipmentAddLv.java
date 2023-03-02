package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lzc
 * @description 主角装备强化（武器、衣服、戒指、项链）
 * @date 2021/4/14 10:53
 */
@Data
public class EPLeaderEquipmentAddLv extends BaseEventParam {
	/** 装备ID 100010：武器，100020：衣服，100030：项链，100040：戒指*/
	private Integer equipmentId;
	private Integer level;

	public static EPLeaderEquipmentAddLv instance(BaseEventParam ep, int equipmentId, int level) {
		EPLeaderEquipmentAddLv ew =new EPLeaderEquipmentAddLv();
		ew.setValues(ep);
		ew.setEquipmentId(equipmentId);
		ew.setLevel(level);
		return ew;
	}
}
