package com.bbw.god.game.sxdh;

import java.io.Serializable;

import com.bbw.god.game.sxdh.config.SxdhRoboterType;

import lombok.Data;

@Data
public class SxdhFightDetail implements Serializable {
	private Integer zoneType; // 战区
	private SxdhRoboterType roboterType; // 玩家机器人类型 见SxdhRoboterType
	private Integer level1; // 座位号1等级
	private Integer level2; // 座位号2等级
	private Integer addScore1 = 0; // 座位号1奖励积分
	private Integer addScore2 = 0;// 座位号2奖励积分
}
