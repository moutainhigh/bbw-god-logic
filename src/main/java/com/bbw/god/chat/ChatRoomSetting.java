package com.bbw.god.chat;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 聊天室设置
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-23 11:40
 */
@Data
public class ChatRoomSetting {
	private int res = 0;// 状态标识，必有0标识成功。其他为错误
	private int serverGroupMaxUser = 10000;
	private int serverRoomMaxUser = 2000;
	private int orgRoomMaxUser = 1000;

	private List<String> serverGroupRoomList = new ArrayList<>();// 区服组聊天室列表
	private List<String> serverRoomList = new ArrayList<>();// 区服聊天室名称列表
	private List<String> guildRoomList = new ArrayList<>();// 行会聊天室列表

}
