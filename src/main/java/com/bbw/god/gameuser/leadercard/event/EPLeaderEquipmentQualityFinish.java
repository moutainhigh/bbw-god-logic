package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lzc
 * @description 主角装备点满星图事件（武器、衣服、戒指、项链）
 * @date 2021/4/14 10:53
 */
@Data
public class EPLeaderEquipmentQualityFinish extends BaseEventParam {
	/** 装备ID 100010：武器，100020：衣服，100030：项链，100040：戒指*/
	private Integer equipmentId;
	/** 品质 （凡品10、中20、上品30、精品40、极品50、仙品60）*/
	private Integer quality;

	public static EPLeaderEquipmentQualityFinish instance(BaseEventParam ep, int equipmentId, int quality) {
		EPLeaderEquipmentQualityFinish ew =new EPLeaderEquipmentQualityFinish();
		ew.setValues(ep);
		ew.setEquipmentId(equipmentId);
		ew.setQuality(quality);
		return ew;
	}
}
