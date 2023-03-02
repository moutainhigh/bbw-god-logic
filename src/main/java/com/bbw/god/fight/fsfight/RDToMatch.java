package com.bbw.god.fight.fsfight;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 神仙大会匹配
 * 
 * @author suhq
 * @date 2019-06-27 09:07:53
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDToMatch extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer zone = null;
	private List<Integer> medicines;
	private List<Integer> zoneSids;// 战区区服
}
