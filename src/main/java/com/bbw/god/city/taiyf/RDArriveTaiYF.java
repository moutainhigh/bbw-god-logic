package com.bbw.god.city.taiyf;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 到达太一府
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveTaiYF extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Integer> specialCells = null;// 太一府捐赠特产格
	/** 是否在梦魇中 */
	private boolean nightmare = false;

}
