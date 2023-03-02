package com.bbw.god.city.chengc.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 建筑等级信息
 *
 * @author suhq
 * @date 2018年10月30日 下午4:20:33
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBuildingInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer type = null;// 建筑类型
	private Integer level = null;// 建筑等级
	private String nextLevelInfo = null;// 建筑下个级别信息
	private Integer remainTimes = null;// 建筑可操作次数，特产铺和府衙无用
	// @JSONField(serialize = false)
	//private List<RDCardPrice> cards = null;// 聚贤庄卡牌
	// @JSONField(serialize = false)
	private List<Integer> eles = null;// 本次将产出的元素
	// @JSONField(serialize = false)
	private Integer kcNum = null;// 矿场产出数量
	private Integer jxNum = null;// 聚贤庄产出点数
	/** 修缮值 */
	private Integer ftRepairValue;
	// !!!注意 必须有一个空的构造函数，否则无法进行将json转为对象
	@SuppressWarnings("unused")
	public RDBuildingInfo() {

	}

	public RDBuildingInfo(int type, int level, String nextLevelInfo) {
		this.type = type;
		this.level = level;
		this.nextLevelInfo = nextLevelInfo;
	}

}
