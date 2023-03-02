package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 装备强化结果
 *
 * @author suhq
 * @date 2021-03-26 17:57
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDEquipmentStrength extends RDCommon {
	private static final long serialVersionUID = 6406202939136667369L;
	/** 是否强化成功 */
    private Integer isSuccess = 0;
}
