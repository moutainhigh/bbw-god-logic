package com.bbw.mc.push;

import com.bbw.mc.NotifyEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 推送对外接口
 *
 * @author suhq
 * @date 2019-08-24 16:10:13
 */
@Service
public class PushAction {
	@Autowired
	private PushReceiverService pushReceiverService;
	@Autowired
	private NotifyEventHandler notifyEventHandler;

	public void cachePushReceiver(long uid, String token, Integer channelId) {
		pushReceiverService.cachePushReceiver(uid, token, channelId);
	}

	public void push(long uid, String title, String content) {
		notifyEventHandler.notify(new PushMsg(Arrays.asList(uid), title, content));
	}

	public void push(List<Long> uids, String title, String content) {
		notifyEventHandler.notify(new PushMsg(uids, title, content));
	}

}
