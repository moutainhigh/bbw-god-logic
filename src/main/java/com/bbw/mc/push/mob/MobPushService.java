package com.bbw.mc.push.mob;

import org.springframework.stereotype.Service;

import com.bbw.mc.push.mob.api.MobPushConfig;
import com.bbw.mc.push.mob.api.exception.ApiException;
import com.bbw.mc.push.mob.api.model.PushWork;
import com.bbw.mc.push.mob.api.push.PushClient;
import com.bbw.mc.push.mob.api.utils.AndroidNotifyStyleEnum;
import com.bbw.mc.push.mob.api.utils.PlatEnum;
import com.bbw.mc.push.mob.api.utils.PushTypeEnum;
import com.bbw.mc.push.mob.api.utils.TargetEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobPushService {
	static {
		MobPushConfig.appkey = "13c3b2e345d0";
		MobPushConfig.appSecret = "0ed719fb0e064c9ee48e62aa1619f0a1";
	}

	/**
	 * mob推送
	 */
	public void push(String title, String content, String... registrationIds) {
//		log.info(registrationIds.toString() + "收到推送" + title);
		PushWork push = new PushWork(PlatEnum.all.getCode(), content, PushTypeEnum.notify.getCode()) // 初始化基础信息
				.buildTarget(TargetEnum._4.getCode(), null, null, registrationIds, null, null) // 设置推送范围
				.buildAndroid(title, AndroidNotifyStyleEnum.normal.getCode(), null, true, true, true) // 定制android样式
				.bulidIos(title, null, null, 1, null, null, null, null) // 定制ios设置
				.buildExtra(1, "{\"key1\":\"value\"}", 1) // 设置扩展信息
		;

		PushClient client = new PushClient();
		try {
			client.sendPush(push);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

}
