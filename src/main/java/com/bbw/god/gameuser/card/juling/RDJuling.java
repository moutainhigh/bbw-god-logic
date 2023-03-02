package com.bbw.god.gameuser.card.juling;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聚灵
 * 
 * @author suhq
 * @date 2019年3月12日 下午6:03:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDJuling extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer addedJxq;// 获得的聚仙旗数，失去为负数
	private Integer addedHsf;// 获得的唤神符数，失去为负数

	public void setSpend(int num, boolean isXD) {
		if (isXD) {
			addedHsf = num;
		} else {
			addedJxq = num;
		}
	}
}
