package com.bbw.mc.push.gexin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.bbw.common.ListUtil;
import com.bbw.mc.push.IChannelPushService;
import com.bbw.mc.push.PushReceiver;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GexinPushService implements IChannelPushService {

	private static final Integer offlineExpireTime = 24 * 3600 * 1000;// 离线有效时间


	@Override
	public void push(List<PushReceiver> receivers, String title, String content) {
		if (ListUtil.isEmpty(receivers)) {
			return;
		}
		IPushResult ret = null;
		try {
			if (receivers.size() == 1) {
				ret = pushToSingle(title, content, receivers.get(0).getToken());
			} else {
				List<String> tokens = receivers.stream().map(PushReceiver::getToken).collect(Collectors.toList());
				ret = pushToMulti(title, content, tokens);
			}
		} catch (Exception e) {
			log.error("推送[" + title + "]出现异常：" + e.getMessage());
		}

//		if (ret != null) {
//			log.info("推送[" + title + "],响应：" + ret.getResponse().toString());
//		} else {
//			log.error("推送[" + title + "],个推服务器响应异常");
//		}
	}

	/**
	 * 多人推送
	 * 
	 * @param title
	 * @param content
	 * @param clientIds
	 */
	private static IPushResult pushToMulti(String title, String content, List<String> clientIds) {
		IGtPush push = new IGtPush(GexinConfig.url, GexinConfig.appKey, GexinConfig.masterSecret);
		NotificationTemplate template = getNotificationTemplate(title, content, 1);
		ListMessage message = new ListMessage();
		message.setData(template);
		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒
		message.setOfflineExpireTime(offlineExpireTime);
		// 配置推送目标
		List<Target> targets = clientIds.stream().map(tmp -> {
			Target target = new Target();
			target.setAppId(GexinConfig.appId);
			target.setClientId(tmp);
			return target;
		}).collect(Collectors.toList());

		// contentId用于在推送时去查找对应的message
		String contentId = push.getContentId(message);
		return push.pushMessageToList(contentId, targets);
	}

	/**
	 * 单人推送
	 * 
	 * @param title
	 * @param content
	 * @param clientId
	 */
	private static IPushResult pushToSingle(String title, String content, String clientId) {
		IGtPush push = new IGtPush(GexinConfig.url, GexinConfig.appKey, GexinConfig.masterSecret);
		NotificationTemplate template = getNotificationTemplate(title, content, 2);
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒
		message.setOfflineExpireTime(offlineExpireTime);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(GexinConfig.appId);
		target.setClientId(clientId);
		// target.setAlias(Alias);
		return push.pushMessageToSingle(message, target);
	}

	/**
	 * 通知模板
	 * 
	 * @param title
	 * @param content
	 * @param TransmissionType 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
	 * @return
	 */
	private static NotificationTemplate getNotificationTemplate(String title, String content, int TransmissionType) {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(GexinConfig.appId);
		template.setAppkey(GexinConfig.appKey);

		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle(title);
		style.setText(content);
		// 配置通知栏图标
		style.setLogo("icon.png");
		// 配置通知栏网络图标
		style.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		// style.setChannel("通知渠道id");
		// style.setChannelName("通知渠道名称");
		// style.setChannelLevel(3); //设置通知渠道重要性
		template.setStyle(style);

		template.setTransmissionType(TransmissionType);
		template.setTransmissionContent("");
		return template;
	}
}
