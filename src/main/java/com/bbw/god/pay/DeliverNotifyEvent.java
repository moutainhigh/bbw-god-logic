package com.bbw.god.pay;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.gameuser.pay.UserReceipt;

/**
 * 产品发放事件
 * 
 * @author suhq
 * @date 2019年3月6日 下午2:24:42
 */
public class DeliverNotifyEvent extends ApplicationEvent {
	private static final long serialVersionUID = -2679976573253821155L;

	public DeliverNotifyEvent(UserReceipt source) {
		super(source);
	}

	/**
	 * 返回充值结果
	 * @return
	 */
	public UserReceipt getParam() {
		return (UserReceipt) this.getSource();
	}
}
