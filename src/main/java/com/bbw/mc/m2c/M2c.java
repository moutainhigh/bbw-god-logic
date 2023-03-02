package com.bbw.mc.m2c;

import lombok.Data;

/**
 * 通知客户端信息
 * 
 * @author suhq
 * @date 2019-06-03 10:15:27
 */
@Data
public class M2c {
	private Long guId = null;// 玩家ID
	private RDM2c info = null;// 要通知的消息
}
