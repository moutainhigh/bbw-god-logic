package com.bbw.god.pay;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 充值回调通知
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-15 14:36
 */

@Getter
@Setter
public class DeliverNotifyParam {
	private int sid;//区服ID
	private int productId;//产品ID
	private int uid;//玩家ID
	private Date purchaseDate;// 充值时间
}
