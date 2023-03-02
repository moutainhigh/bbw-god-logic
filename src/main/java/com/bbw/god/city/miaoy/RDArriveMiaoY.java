package com.bbw.god.city.miaoy;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 到达鹿台
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveMiaoY extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer hexagram = 0;
}
