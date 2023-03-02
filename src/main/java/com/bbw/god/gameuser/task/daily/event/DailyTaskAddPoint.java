package com.bbw.god.gameuser.task.daily.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午11:13:05 
* 类说明  每日任务积分增加 事件
*/
@Getter
@Setter
public class DailyTaskAddPoint extends BaseEventParam{
	private Integer point=0;
	
	public static DailyTaskAddPoint instance(BaseEventParam bep,Integer point) {
		DailyTaskAddPoint dta=new DailyTaskAddPoint();
		dta.setValues(bep);
		dta.setPoint(point);
		return dta;
	}

}
