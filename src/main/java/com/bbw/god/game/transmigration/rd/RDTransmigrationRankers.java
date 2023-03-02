package com.bbw.god.game.transmigration.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 独立排行列表
 *
 * @author: suhq
 * @date: 2021/9/15 11:58 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDTransmigrationRankers extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer myRank = 0;// 我的排名
	private Integer myValue = 0;// 我的数值
	private Integer firstValue = 0;// 第一名的数值
	private Integer beforerValue = 0;// 前一名的数值
	private Integer totalSize = 0;// 排行榜总人数
	private List<RDTransmigrationRanker> rankers = new ArrayList<>();

}
