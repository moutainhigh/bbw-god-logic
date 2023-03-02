package com.bbw.god.city.yousg;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达游商馆
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveYouSG extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Integer> specials = null;;// 可购买的特产

}
