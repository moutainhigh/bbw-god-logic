package com.bbw.god.city.chengc.in;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 建筑升级信息
 * 
 * @author suhq
 * @date 2018年11月15日 下午2:10:04
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBuildingUpdateInfo extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nextLevelInfo = null;// 建筑下个级别信息
	private Integer remainTimes = null;// 建筑可操作次数，特产铺和府衙无用
	private Integer special = null;// 升级特产铺解锁的新特产
	private Integer ele = null;// 升级矿产多产出的元素
	private Integer kcNum = null;// 矿场产出数量
	private Integer updateFull = null;// 建筑是否升满
	/** 法坛是否升级成功 */
	private boolean isUpdate = true;

}
