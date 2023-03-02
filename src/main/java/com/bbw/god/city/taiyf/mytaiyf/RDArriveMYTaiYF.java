package com.bbw.god.city.taiyf.mytaiyf;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 到达梦魇世界太一府
 * 
 * @author lzc
 * @date 2021年3月19日
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveMYTaiYF extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Integer> specialCells = null;// 已捐特产
	private List<Integer> roundSpecialCells = null;// 本轮特产列表
	/** 是否在梦魇中 */
	private boolean nightmare = true;
}
