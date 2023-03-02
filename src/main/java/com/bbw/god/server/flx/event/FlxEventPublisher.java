package com.bbw.god.server.flx.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 富临轩事件推送发布器
 * @date 2020/2/24 15:08
 */
public class FlxEventPublisher {
	public static void pubCaiShuZiWinEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new CaiShuZiWinEvent(new EPCaiShuZiWin(bep)));
	}

	public static void pubCaiShuZiFailEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new CaiShuZiFailEvent(new EPCaiShuZiFail(bep)));
	}

	public static void pubYaYaLeWinEvent(int type, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new YaYaLeWinEvent(new EPYaYaLeWin(type, bep)));
	}

	public static void pubYaYaLeFailEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new YaYaLeFailEvent(new EPYaYaLeFail(bep)));
	}

	public static void pubCaiShuZiBetEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new CaiShuZiBetEvent(new EPCaiShuZiBet(bep)));
	}

	public static void pubYaYaLeBetEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new YaYaLeBetEvent(new EPYaYaLeBet(bep)));
	}
}
