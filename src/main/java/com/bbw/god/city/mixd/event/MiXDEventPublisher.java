package com.bbw.god.city.mixd.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 梦魇迷仙洞事件发布器
* @author 作者 ：lzc
* @version 创建时间：2021年06月07日
* 类说明 
*/

public class MiXDEventPublisher {

	/**
	 * 饮用泉水
	 * @param event
	 */
	public static void pubDrinkWaterEvent(EPDrinkWater event) {
		SpringContextUtil.publishEvent(new DrinkWaterEvent(event));
	}

	/**
	 * 踩到陷阱
	 * @param event
	 */
	public static void pubStepTrapEvent(BaseEventParam event) {
		SpringContextUtil.publishEvent(new StepTrapEvent(event));
	}

	/**
	 * 打开特殊宝箱事件
	 * @param event
	 */
	public static void pubOpenSpecialBoxEvent(BaseEventParam event) {
		SpringContextUtil.publishEvent(new OpenSpecialBoxEvent(event));
	}

	/**
	 * 通过一层事件
	 * @param event
	 */
	public static void pubPassTierEvent(EPPassTier event) {
		SpringContextUtil.publishEvent(new PassTierEvent(event));
	}

	/**
	 * 熔炼事件
	 * @param event
	 */
	public static void pubSmeltEvent(EPSmelt event) {
		SpringContextUtil.publishEvent(new SmeltEvent(event));
	}

	/**
	 * 层主击败挑战者事件
	 * @param event
	 */
	public static void pubCZBeatDefierEvent(BaseEventParam event) {
		SpringContextUtil.publishEvent(new CZBeatDefierEvent(event));
	}

	/**
	 * 层主被打败事件
	 * @param event
	 */
	public static void pubCZBiteTheDustEvent(BaseEventParam event) {
		SpringContextUtil.publishEvent(new CZBiteTheDustEvent(event));
	}

	/**
	 * 进入梦魇迷仙洞
	 * @param uid
	 */
	public static void pubIntoNightmareMxdEvent(long uid) {
		BaseEventParam event=new BaseEventParam(uid);
		SpringContextUtil.publishEvent(new IntoNightmareMxdEvent(event));
	}
}
