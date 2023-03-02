package com.bbw.mc.broadcast;

import java.util.Date;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.Person;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-20 14:10
 */
@Data
public class BroadcastMsg extends Msg {
	private int group = 0;// 区服组，如果group>0,忽略sid;
	private int sid = 0;// 区服ID
	private Date overTime;// 过期时间
	/**
	 * 及时消息，10分钟内显示
	 */
	private static int timely_period = 60;// 分钟

	/**
	 * 及时广播
	 * 
	 * @param msgContent:广播内容
	 * @param period:广播时长，单位：分钟
	 * @return
	 */
	public static BroadcastMsg timelyMsg(int sid, String msgContent, int period) {
		BroadcastMsg msg = new BroadcastMsg();
		msg.setId(ID.INSTANCE.nextId());
		msg.setType(MsgType.BROADCAST);
		msg.setPerson(Person.GameUser);
		msg.setSid(sid);
		msg.setContent(msgContent);
		if (period > 0) {
			msg.setOverTime(DateUtil.addMinutes(DateUtil.now(), period));
		} else {
			msg.setOverTime(DateUtil.addMinutes(DateUtil.now(), timely_period));
		}
		return msg;
	}

	public static BroadcastMsg timelyMsg(int sid, String msgContent) {
		return timelyMsg(sid, msgContent, timely_period);
	}

	public static BroadcastMsg timelyGroupMsg(int group, String msgContent, int period) {
		BroadcastMsg msg = new BroadcastMsg();
		msg.setId(ID.INSTANCE.nextId());
		msg.setType(MsgType.BROADCAST);
		msg.setPerson(Person.GameUser);
		msg.setGroup(group);
		msg.setContent(msgContent);
		if (period > 0) {
			msg.setOverTime(DateUtil.addMinutes(DateUtil.now(), period));
		} else {
			msg.setOverTime(DateUtil.addMinutes(DateUtil.now(), timely_period));
		}
		return msg;
	}

	public static BroadcastMsg timelyGroupMsg(int group, String msgContent) {
		return timelyMsg(group, msgContent, timely_period);
	}

	public boolean ifToAllServer() {
		return group == 0 && sid == 0;
	}

	public boolean ifToServer() {
		return sid > 0;
	}

	public boolean ifToGroup() {
		return group > 0;
	}
}
