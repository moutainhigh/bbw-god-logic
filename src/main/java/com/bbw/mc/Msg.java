package com.bbw.mc;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-20 14:08
 */
@Data
public class Msg {
	protected Long id;
	protected MsgType type;// 消息类型
	protected Person person;// 发送目标类型
	protected String content;// 发送内容
}
