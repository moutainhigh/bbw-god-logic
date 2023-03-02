package com.bbw.god.city.chengc.in;

import com.bbw.common.StrUtil;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 多个建筑产出信息
 * 
 * @author suhq
 * @date 2019年3月12日 下午3:24:41
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBuildingOutputs extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private RDBuildingOutput kcOutput = null;
	private RDBuildingOutput qzOutput = null;
	private RDBuildingOutput lblOutput = null;
	private RDBuildingOutput ldfOutput = null;
	private RDBuildingOutput jxzOutput = null;

	public void copyNotice(RDBuildingOutput output) {
		// 达成的成就
		if (output.getTaskIds() != null) {
			if (getTaskIds() == null) {
				setTaskIds(new ArrayList<>());
			}
			getTaskIds().addAll(output.getTaskIds());
		}
		// 每日任务达成通知
		setDailyTaskStatus(output.getDailyTaskStatus());
		// 新手进阶任务通知
		setGrowTaskStatus(output.getGrowTaskStatus());
		// 主线任务达成通知
		setMainTaskStatus(output.getMainTaskStatus());
		// 激活的助力礼包
		// setActiveZLLB(output.getActiveZLLB());
	}

	/**
	 * 单个建筑产出信息
	 * 
	 * @author suhq
	 * @date 2019年3月12日 下午3:24:55
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class RDBuildingOutput extends RDCommon implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer rate = null;// 暴击
		private String message = null;// 建筑领取信息

		/**
		 * 兼容旧版本
		 * 
		 * @param message
		 */
		public void setMessage(String message) {
			this.message = message;
			if (StrUtil.isNotBlank(message)) {
				setRes(1);
			} else {
				setRes(0);
			}

		}
	}

}
