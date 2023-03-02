package com.bbw.god.mall.cardshop;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 抽卡
 * 
 * @author suhq
 * @date 2019-05-08 08:56:14
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardDraw extends RDCommon {
	// 获得的许愿值
	private Integer addedVow;
	// 许愿卡
	private Integer vowCardId = -1;
}
