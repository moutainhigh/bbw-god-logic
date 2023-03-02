package com.bbw.god.city.miaoy.hexagram.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/** 64卦
* @author 作者 ：lzc
* @version 创建时间：2021年04月13日
* 类说明 
*/
@Getter
@Setter
public class EPHexagram extends BaseEventParam{
	private Integer hexagramId;//卦象ID
	private boolean newHexagram;//是否为新卦象

	public static EPHexagram instance(BaseEventParam ep, Integer hexagramId,boolean isNewHexagram) {
		EPHexagram ew=new EPHexagram();
		ew.setValues(ep);
		ew.setHexagramId(hexagramId);
		ew.setNewHexagram(isNewHexagram);
		return ew;
	}
}
