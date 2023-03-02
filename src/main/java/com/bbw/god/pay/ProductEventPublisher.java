package com.bbw.god.pay;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.gameuser.pay.UserReceipt;

/**
 * 产品事件发送器
 * 
 * @author suhq
 * @date 2019年3月6日 下午2:26:26
 */
public class ProductEventPublisher {

	/**
	 * 通知产品发放
	 * 
	 * @param UserReceipt
	 */
	public static void pubDeliverEvent(UserReceipt receipt) {
		SpringContextUtil.publishEvent(new DeliverNotifyEvent(receipt));
	}

}
