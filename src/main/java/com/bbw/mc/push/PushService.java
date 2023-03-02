package com.bbw.mc.push;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.bbw.mc.push.ios.IOSPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.NotifyService;
import com.bbw.mc.push.gexin.GexinPushService;

import lombok.extern.slf4j.Slf4j;

/**
 * 推送服务
 * 
 * @author suhq
 * @date 2019-08-20 09:19:27
 */
@Slf4j
@Service
public class PushService extends NotifyService {
	@Autowired
	private PushReceiverService pushReceiverService;
	@Autowired
	private GexinPushService gexinPushService;
	@Autowired
	private IOSPushService iosPushService;

	PushService() {
		this.type = MsgType.PUSH;
	}

	@Override
	public void notify(Msg msg) {
		if (msg instanceof PushMsg) {
			PushMsg pushMsg = (PushMsg) msg;
			push(pushMsg.getUids(), pushMsg);
			return;
		}
		log.error("无效的推送数据" + msg.toString());
	}

	private void push(List<Long> uids, PushMsg pushMsg) {
		// 空集合跳过
		if (ListUtil.isEmpty(uids)) {
			return;
		}
		List<String> pushTokens = new ArrayList<>();
		List<PushReceiver> receivers = new ArrayList<PushReceiver>();
		for (Long uid : uids) {
			PushReceiver receiver = pushReceiverService.getPushReceiver(uid);
			if (receiver == null || StrUtil.isBlank(receiver.getToken())) {
				continue;
			}
			// 同一条消息避免给统一设备重复发放（多账号，多角色）
			if (pushTokens.contains(receiver.getToken())) {
				continue;
			}
			pushTokens.add(receiver.getToken());
			receivers.add(receiver);
		}
		// 推送
		push(receivers,pushMsg.getTitle(), pushMsg.getContent());
	}

	private void push(List<PushReceiver> receivers, String title, String content) {
		List<PushReceiver> iosReceivers = new ArrayList<>();
		List<PushReceiver> others = new ArrayList<>();
		receivers.forEach(tmp->{
			if (iosPushService.isMatched(tmp.getCId())){
				iosReceivers.add(tmp);
			}else{
				others.add(tmp);
			}
		});
		if (ListUtil.isNotEmpty(iosReceivers)){
			iosPushService.push(iosReceivers,title,content);
		}
		if (ListUtil.isNotEmpty(others)){
			gexinPushService.push(others,title,content);
		}
	}

}
