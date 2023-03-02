package com.bbw.god.gameuser.task;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.UserCfgObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务提前生成
 *
 * @author suhq 2018年10月9日 上午8:54:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
//@Deprecated
public abstract class UserTask extends UserCfgObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer needValue;
	private long value = 0;
	private Integer status = TaskStatusEnum.DOING.getValue();
	private long accomplishTime = 0;

	public void addValue(long addValue) {
		this.value += addValue;
		if (this.value >= this.needValue) {
			this.value = this.needValue;
			this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
			this.accomplishTime = DateUtil.toDateTimeLong();
		}
	}

	public void updateValue(int value) {
		this.value = value;
		if (this.value >= needValue) {
			this.value = this.needValue;
			this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
			this.accomplishTime = DateUtil.toDateTimeLong();
		}
	}

	/**
	 * 任务是否达成
	 * 
	 * @return
	 */
	public boolean ifAccomplished() {
		return status >= TaskStatusEnum.ACCOMPLISHED.getValue();
	}

	/**
	 * 任务是否领取
	 * 
	 * @return
	 */
	public boolean ifAwarded() {
		return status == TaskStatusEnum.AWARDED.getValue();
	}
}
