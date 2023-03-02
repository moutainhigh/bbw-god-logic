package com.bbw.god.city.fud;

import java.io.Serializable;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达福地
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveFuD extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer addedGold = null;// 元宝

	public static RDArriveFuD fromRDCommon(RDCommon rd) {
		RDArriveFuD rdArrive = new RDArriveFuD();
		rdArrive.setAddedGold(rd.getAddedGold());
		// 顶层福地获得的元宝数据置空
		rd.setAddedGold(null);
		return rdArrive;
	}

}
