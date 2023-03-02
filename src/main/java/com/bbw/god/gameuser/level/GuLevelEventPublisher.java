package com.bbw.god.gameuser.level;

import com.bbw.common.SpringContextUtil;

/**
 * 玩家等级事件发布器
 * 
 * @author suhq
 * @date 2019-10-18 14:48:07
 */
public class GuLevelEventPublisher {

	public static void pubLevelUpEvent(EPGuLevelUp ep) {
		SpringContextUtil.publishEvent(new GuLevelUpEvent(ep));
	}

}
