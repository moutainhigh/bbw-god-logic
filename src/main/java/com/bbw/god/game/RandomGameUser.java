package com.bbw.god.game;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-29 11:33
 */
@Setter
@Getter
public class RandomGameUser {
	private long gameUserId;
	private int serverId;
	private int minLevel;
	private int maxLevel;
}
