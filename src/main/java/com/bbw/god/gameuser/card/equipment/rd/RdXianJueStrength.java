package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 装备强化结果
 *
 * @author: huanghb
 * @date: 2022/9/19 14:29
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdXianJueStrength extends RDCommon {
	private static final long serialVersionUID = 6406202939136667369L;
	/** 是否强化成功 */
	private Integer isSuccess = 0;
}
