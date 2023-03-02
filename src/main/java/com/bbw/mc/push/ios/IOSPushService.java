package com.bbw.mc.push.ios;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bbw.App;
import com.bbw.common.ListUtil;
import com.bbw.mc.push.IChannelPushService;
import com.bbw.mc.push.PushReceiver;
import javapns.Push;
import javapns.notification.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@ConfigurationProperties(prefix="push")
@Service
public class IOSPushService implements IChannelPushService {
	@Setter
	private List<IOSCertificate> iosChannels;
	@Setter
	private Integer numOfThread;
	@Autowired
	private App app;

	@Override
	public void push(List<PushReceiver> receivers, String title, String content) {
		if (ListUtil.isEmpty(receivers)){
			return;
		}
		try{
			Map<Integer,List<PushReceiver>> channelReceiverMap = receivers.stream().collect(Collectors.groupingBy(PushReceiver::getCId));
			PushNotificationPayload payLoad = new PushNotificationPayload();
			payLoad.addAlert(title);
			payLoad.addBadge(1);//应用图标上小红圈上的数值     //
			payLoad.addSound("default");
			for (Integer channel:channelReceiverMap.keySet()) {
				IOSCertificate  certificate = getIOSCertificate(channel);
				if (certificate == null){
					continue;
				}
				List<String> tokens = channelReceiverMap.get(channel).stream().map(PushReceiver::getToken).collect(Collectors.toList());
				PushedNotifications result = Push.payload(payLoad, certificate.getCertificate(), certificate.getPwd(), app.runAsProd(), numOfThread, tokens);
				//失败集合
				PushedNotifications failedNotifications = result.getFailedNotifications();
//				System.out.println("ios_"+channel + "失败数量:" + failedNotifications.size());
				failedNotifications.stream().map(fail -> fail.getDevice()).collect(Collectors.toList()).forEach(token -> log.info("失败token:" + token));
				//成功集合
				PushedNotifications successfulNotifications = result.getSuccessfulNotifications();
//				System.out.println("ios_"+channel + "成功数量:" + successfulNotifications.size());
			}

		}catch (JSONException e){
			e.printStackTrace();
			log.error(e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}

	}

	public boolean isMatched(int channel){
		return getIOSCertificate(channel) != null;
	}
	private IOSCertificate getIOSCertificate(int channel){
		if (ListUtil.isEmpty(iosChannels)){
			return null;
		}
		return iosChannels.stream().filter(tmp->tmp.getChanel() == channel).findFirst().orElse(null);
	}
}
