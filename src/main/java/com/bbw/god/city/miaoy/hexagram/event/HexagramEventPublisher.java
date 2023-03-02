package com.bbw.god.city.miaoy.hexagram.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 64卦事件发布器
* @author 作者 ：lzc
* @version 创建时间：2021年04月13日
* 类说明 
*/

public class HexagramEventPublisher {

	/**
	 * 64卦事件
	 * @param event
	 */
	public static void pubHexagramEvent(EPHexagram event) {
		SpringContextUtil.publishEvent(new HexagramEvent(event));
	}

	public static void pubHexagramBuffDeductEvent(BaseEventParam bep,int hexagramId,int times) {
		SpringContextUtil.publishEvent(new HexagramBuffDeductEvent(EPHexagramBuffDeduct.instance(bep,hexagramId,times)));
	}

}
