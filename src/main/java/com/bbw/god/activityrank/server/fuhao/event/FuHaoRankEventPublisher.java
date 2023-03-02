package com.bbw.god.activityrank.server.fuhao.event;

import com.bbw.common.SpringContextUtil;

/**
 * @author suchaobin
 * @description 富豪榜排名事件监听器
 * @date 2020/2/5 9:51
 */
public class FuHaoRankEventPublisher {
	public static void pubFuHaoRankUpEvent(EPFuHaoRankUp epFuHaoRankUp) {
		SpringContextUtil.publishEvent(new FuHaoRankUpEvent(epFuHaoRankUp));
	}
}
