package com.bbw.god.city.chengc.in;

import com.bbw.god.city.chengc.RDTradeInfo;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 城内信息
 *
 * @author suhq
 * @date 2018年10月30日 下午4:20:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCityInInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer cityId = null;
	private Integer isAblePromote = null;// 是否可进阶
	private List<RDBuildingInfo> info = null;// 建筑等级信息
	private Integer useDefaultKcEles = null;// 是否设置默认元素领取顺序
	private List<Integer> defaultKcEles = null;// 默认元素领取顺序
	private Integer ldfCard = null;// 炼丹房的卡牌
	private Integer remainUpdateTimes = 1;
	private Integer areaId = null;// 城池所在区域
	private Integer transmigrationScore = null;
	/** 城市出售特产 */
	private List<RDTradeInfo.RDCitySpecial> citySpecials;
}
