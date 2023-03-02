package com.bbw.god.gameuser.task.fshelper.event;

import com.bbw.common.SpringContextUtil;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午11:19:00 
* 类说明 
*/
public class TaskEventPublisher {
	

	public static void pubEpFsHelperChangeEvent(EpFsHelperChange dta) {
		SpringContextUtil.publishEvent(new EpFsHelperChangeEvent(dta));
	}
}
