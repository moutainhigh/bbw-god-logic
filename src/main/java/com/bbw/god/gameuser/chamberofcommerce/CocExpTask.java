package com.bbw.god.gameuser.chamberofcommerce;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.gameuser.chamberofcommerce.RDCoc.CocReward;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月2日 下午4:30:59 
* 类说明 
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CocExpTask implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;// 任务Id
	private Integer type;// 类型
	private String content;// 文字说明
	private Long target;// 目标量
	private Integer status = CocConstant.EXP_TASK_STATUS_DOING; // 状态
	private String progress = "";// 完成比例
	private List<CocReward> rewards = null;// 客户端时需要
	public void setProgressStr(long p) {
		if (p >= target) {
			progress = target + "/" + target;
			status = CocConstant.EXP_TASK_STATUS_FINISHED;
		} else {
			progress = p + "/" + target;
		}
	}
}