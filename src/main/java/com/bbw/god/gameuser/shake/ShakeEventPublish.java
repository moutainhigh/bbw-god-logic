package com.bbw.god.gameuser.shake;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

import java.util.List;

/**
 * @author suchaobin
 * @description 丢骰子事件推送发布器
 * @date 2020/2/24 11:03
 */
public class ShakeEventPublish {
	public static void pubShakeEvent(List<Integer> shakeList, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new ShakeEvent(new EPShake(shakeList, bep)));
	}
}
