package com.bbw.god.gameuser.chamberofcommerce.event;

import com.bbw.common.SpringContextUtil;

/**
 * 商会事件发布器
 *
 * @author lwb
 * @version 1.0
 * @date 2019年6月24日
 */
public class CocEventPublisher {

	public static void pubCocTaskFinishedEvent(EPTaskFinished ev) {
		SpringContextUtil.publishEvent(new CocTaskFinishedEvent(ev));
	}

	public static void pubUplevelAddEvent(EPCocUpLv ev) {
		SpringContextUtil.publishEvent(new CocUpLvEvent(ev));
	}

	public static void pubBuyCocBagEvent(EPBuyCocBag ev) {
		SpringContextUtil.publishEvent(new BuyCocBagEvent(ev));
	}
}
