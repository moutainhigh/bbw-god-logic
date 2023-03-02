package com.bbw.god.city.xianrd;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDTreasureInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达仙人洞
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveXianRD extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer addedGoldEle = null;// 金元素
	private Integer addedWoodEle = null;// 木元素
	private Integer addedWaterEle = null;// 水元素
	private Integer addedFireEle = null;// 火元素
	private Integer addedEarthEle = null;// 土元素
	private List<RDTreasureInfo> treasures = null;// 法宝

	public static RDArriveXianRD fromRDCommon(RDCommon rd) {
		RDArriveXianRD rdArrive = new RDArriveXianRD();
		rdArrive.setAddedGoldEle(rd.getAddedGoldEle());
		rdArrive.setAddedWoodEle(rd.getAddedWoodEle());
		rdArrive.setAddedWaterEle(rd.getAddedWaterEle());
		rdArrive.setAddedFireEle(rd.getAddedFireEle());
		rdArrive.setAddedEarthEle(rd.getAddedEarthEle());
		rdArrive.setTreasures(rd.getTreasures());

		// 顶层仙人洞获得的元素、法宝数据置空
		rd.setAddedGoldEle(null);
		rd.setAddedWoodEle(null);
		rd.setAddedWaterEle(null);
		rd.setAddedFireEle(null);
		rd.setAddedEarthEle(null);
		rd.setTreasures(null);

		return rdArrive;
	}

}
