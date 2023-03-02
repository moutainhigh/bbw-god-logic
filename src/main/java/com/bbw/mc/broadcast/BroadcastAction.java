package com.bbw.mc.broadcast;

import com.bbw.mc.NotifyEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 推送地外接口
 *
 * @author suhq
 * @date 2019-08-24 15:54:20
 */
@Service
public class BroadcastAction {
	@Autowired
	private NotifyEventHandler notifyEventHandler;

	public void broadcast(int sid, String broadcastInfo) {
		notifyEventHandler.notify(BroadcastMsg.timelyMsg(sid, broadcastInfo));
	}

	public void broadcast(int sid, String broadcastInfo, int peroid) {
		notifyEventHandler.notify(BroadcastMsg.timelyMsg(sid, broadcastInfo, peroid));
	}

}
