package com.bbw.god.gameuser.card.juling;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聚灵界信息
 * 
 * @author suhq
 * @date 2019年2月28日 上午9:23:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdJuLJ extends RDSuccess implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Integer> jlCards;
	private Integer needGold = null;// 难度
	private Integer needJxq = null;
	private Integer jxq = null;

	private List<Integer> jlCardsXd;
	private Integer needGoldXd = null;// 难度
	private Integer needHsf = null;
	private Integer hsf = null;
}
