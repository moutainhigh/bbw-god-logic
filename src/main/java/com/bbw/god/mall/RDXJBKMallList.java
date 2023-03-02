package com.bbw.god.mall;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDXJBKMallList extends RDMallList implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer ownSsNum = null;// 神沙拥有数
	private Integer ownHyNum = null;// 魂源拥有数量
	/** 仙玉拥有数量 */
	private Integer ownXyNum = 0;

}
