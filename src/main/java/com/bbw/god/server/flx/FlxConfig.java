package com.bbw.god.server.flx;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Configuration
@Component
public class FlxConfig {
	// 数馆倍率
	private Integer sgOdds = 36;
	// 元素馆需要的元宝
	private Integer ysgNeedGold = 10;
	// 铜钱投注每日限制
	private Integer copperDayLimit = 50000;
	// 元宝投注每日限制
	private Integer goldDayLimit = 2000;
	// 数馆元宝投注等级限制
	private Integer sgGoldBetLevelLimit = 15;

}
