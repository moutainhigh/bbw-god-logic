package com.bbw.god.city.mixd.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 梦魇迷仙洞饮用泉水
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/
@Getter
@Setter
public class EPDrinkWater extends BaseEventParam{
	private Integer blood;//饮用泉水后的血量

	public static EPDrinkWater instance(BaseEventParam ep, int blood) {
		EPDrinkWater ew=new EPDrinkWater();
		ew.setValues(ep);
		ew.setBlood(blood);
		return ew;
	}
}
