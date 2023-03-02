package com.bbw.god.pay;

import java.util.Date;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-30 17:04
 */
@Data
public class ResetDate {
	private Date lastResetDate;//上次首购重置时间
	private Date thisResetDate;//本次首购重置时间
}
