package com.bbw.god.city.mixd.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 通过一层梦魇迷仙洞
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/
@Getter
@Setter
public class EPPassTier extends BaseEventParam{
	private Integer blood;//剩余血量
	private boolean isFullBlood;//是否本层没有扣过血

	public static EPPassTier instance(BaseEventParam ep, int blood, boolean isFullBlood) {
		EPPassTier ew=new EPPassTier();
		ew.setValues(ep);
		ew.setBlood(blood);
		ew.setFullBlood(isFullBlood);
		return ew;
	}
}
