package com.bbw.god.city.mixd.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 *梦魇迷仙洞熔炼
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/
@Getter
@Setter
public class EPSmelt extends BaseEventParam{
	private boolean result;//熔炼结果

	public static EPSmelt instance(BaseEventParam ep, boolean result) {
		EPSmelt ew=new EPSmelt();
		ew.setValues(ep);
		ew.setResult(result);
		return ew;
	}
}
