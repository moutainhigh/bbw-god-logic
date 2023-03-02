package com.bbw.god.city.yeg.event;

import com.bbw.common.SpringContextUtil;

/**
 * @author suchaobin
 * @description 野怪事件发送器
 * @date 2020/5/9 11:27
 **/
public class YeGuaiEventPublisher {
	public static void pubOpenYeGuaiBoxEvent(EPOpenYeGuaiBox ep) {
		SpringContextUtil.publishEvent(new OpenYeGuaiBoxEvent(ep));
	}
}
