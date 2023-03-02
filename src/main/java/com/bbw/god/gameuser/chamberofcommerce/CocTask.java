package com.bbw.god.gameuser.chamberofcommerce;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.gameuser.chamberofcommerce.RDCoc.CocReward;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 商会任务
* @author lwb  
* @date 2019年4月15日  
* @version 1.0  
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CocTask implements Serializable {
	private static final long serialVersionUID = 1L;
	private int taskId;
	private int level;//任务级别
	private int targetAreaId;//派送区域ID
	private String targetAreaName;//派送区域ID
	private int targetCityId=0;//派送城市ID
	private String targetCityName;//派送城市ID
	private int status = CocConstant.TASK_STATUS_WAIT;// 状态 默认为待接受状态
	private int urgent = 0;// 是否加急
	private String taskExplain;//任务文字描述
	private List<Task> targetSpecial;//任务要求
	private List<CocReward> rewards = null;// 客户端时需要

	/**
	 * 商会任务明细
	* @author lwb  
	* @date 2019年4月21日  
	* @version 1.0
	 */
	@Data
	public static class Task{
		private int specialId;//特产id
		private String specialName;
		private int num;//特产数量
		private int process=0;//进度
	}
	
	/**
	 * 商会 任务特产
	* @author lwb  
	* @date 2019年4月21日  
	* @version 1.0
	 */
	@Data
	public static class CocSpecial{
		private int id;
		private int areaId;
		private String name;
	}

}
