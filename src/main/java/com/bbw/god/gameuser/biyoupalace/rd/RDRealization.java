package com.bbw.god.gameuser.biyoupalace.rd;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 领悟
 * 
 * @author suhq
 * @date 2019-09-27 16:28:01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDRealization extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer useNum = null;// 消耗的数量
	private Integer addedProgress = null;// 添加的点数
	private Integer status = null;// 状态
	private Integer secretBiography=null;

}
