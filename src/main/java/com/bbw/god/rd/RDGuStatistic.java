package com.bbw.god.rd;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色统计信息
 * 
 * @author suhq
 * @date 2019年3月12日 下午4:42:03
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDGuStatistic extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Integer> unfilledSpecialIds = null;// 太一府未捐赠的特产
	private List<Integer> nightmareUnfilledSpecialIds = null;// 梦魇太一府未捐赠的特产
	private Integer weekCopperRank = null;// 富豪榜排行
	private Integer pvpRank = null;// 封神台排行
	private Integer tyfFillCount = null;// 太一府捐赠数
	private Integer satisfaction = null;// 女娲庙捐献好感度

}
