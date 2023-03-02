package com.bbw.mc;

/**
 * 消息通知类，所有的消息需实现该类
 * 
 * @author suhq
 * @date 2019-08-24 13:51:08
 */
public abstract class NotifyService {
	protected MsgType type;

	public abstract void notify(Msg msg);

	public boolean isSupport(MsgType msgType) {
		return this.type == msgType;
	}

}
