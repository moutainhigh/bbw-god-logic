package com.bbw.god.rd;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 购买体力
 * 
 * @author suhq
 * @date 2019年3月12日 下午4:48:17
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDiceBuy extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer diceBuyTimes = null;// 体力购买次数

}
