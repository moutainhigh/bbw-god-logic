package com.bbw.god.game.sxdh.config;

import java.io.Serializable;

import lombok.Data;

/**
 * 神仙大会战区
 * 
 * @author suhq
 * @date 2019-06-18 10:29:04
 */
@Data
public class CfgSxdhZoneEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Integer minOpenDay;// 多少天后
	private Integer maxOpenDay = Integer.MAX_VALUE;// 多少天内
	private Integer openAfterDay;// 多少天后开启
	private Integer initBlood;// 初始化血量
}
