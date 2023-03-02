package com.bbw.god.pay;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式:
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-30 14:35
 */
@Getter
@AllArgsConstructor
public enum PayType {
	ChannelPay(0, "ChannelPay"), //渠道自己的支付方式
	WxPayApp(1, "WxPayApp"), //微信APP支付
	WxPayH5(3, "WxPayH5"), //微信H5
	WxPayJSAPI(5, "WxPayJSAPI"), //微信JSAPI,公众号，小程序
	WxPayMiniGame(7, "WxPayMiniGame"), //微信小游戏
	AliPayApp(2, "AliPayApp"), //支付宝APP
	AliPayH5(4, "AliPayH5");//支付宝H5

	private int value;
	private String name;

	public static PayType fromValue(int value) {
		for (PayType item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		throw CoderException.fatal("没有键值为[" + value + "]的数据类型！");
	}
}
