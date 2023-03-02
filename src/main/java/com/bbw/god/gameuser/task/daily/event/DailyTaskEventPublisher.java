package com.bbw.god.gameuser.task.daily.event;

import com.bbw.common.SpringContextUtil;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午11:19:00 
* 类说明 
*/
public class DailyTaskEventPublisher {
	
	public static void pubDailyTaskAddPotintEvent(DailyTaskAddPoint dta) {
		SpringContextUtil.publishEvent(new DailyTaskAddPointEvent(dta));
	}
}
