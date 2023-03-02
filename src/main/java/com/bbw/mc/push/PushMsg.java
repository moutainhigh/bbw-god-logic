package com.bbw.mc.push;

import java.util.List;

import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;

import lombok.Data;

/**
 * 推送消息
 * 
 * @author suhq
 * @date 2019-08-23 17:02:01
 */
@Data
public class PushMsg extends Msg {
	private List<Long> uids;
	private String title;

	public PushMsg(List<Long> uids, String title, String content) {
		this.type = MsgType.PUSH;
		this.uids = uids;
		this.title = title;
		this.content = content;
	}
}
