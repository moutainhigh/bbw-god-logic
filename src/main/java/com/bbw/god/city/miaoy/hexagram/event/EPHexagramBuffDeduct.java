package com.bbw.god.city.miaoy.hexagram.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 64卦BUFF 次数扣除
* @author 作者 ：lzc
* @version 创建时间：2021年04月13日
* 类说明 
*/
@Getter
@Setter
public class EPHexagramBuffDeduct extends BaseEventParam{
	private Integer hexagramId;//卦象ID
	private Integer times;//次数

	public static EPHexagramBuffDeduct instance(BaseEventParam ep, int hexagramId, int times) {
		EPHexagramBuffDeduct ew=new EPHexagramBuffDeduct();
		ew.setValues(ep);
		ew.setHexagramId(hexagramId);
		times=times>0?-times:times;
		ew.setTimes(times);
		return ew;
	}
}
