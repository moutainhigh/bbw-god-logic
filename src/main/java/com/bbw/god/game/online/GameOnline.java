package com.bbw.god.game.online;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-08 17:09
 */
@Data
public class GameOnline {
	private String appid = "ru1b5is4yn9ky9iq";
	private List<ServerOnline> onlines = new ArrayList<>();

	@Data
	public static class ServerOnline {
		private String server = "未设置";
		private int online = 0;
		private long timestamp = 0;
	}
}
