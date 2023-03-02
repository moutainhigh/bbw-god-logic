package com.bbw.god.city.mixd.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/** 梦魇迷仙洞饮用泉水事件
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/
public class DrinkWaterEvent extends ApplicationEvent implements IEventParam{

	private static final long serialVersionUID = 1L;

	public DrinkWaterEvent(EPDrinkWater source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPDrinkWater getEP() {
		return (EPDrinkWater)getSource();
	}
}
