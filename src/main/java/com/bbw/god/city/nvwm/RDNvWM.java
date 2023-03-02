package com.bbw.god.city.nvwm;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 女娲庙捐献
 * 
 * @author suhq
 * @date 2019年3月12日 上午11:40:38
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDNvWM extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 女娲庙捐献好感度 */
	private Integer satisfaction = null;
	/** 增加的泥人进度值（梦魇女娲庙） */
	private Integer addProgressToPinchPeopleValue;

}
