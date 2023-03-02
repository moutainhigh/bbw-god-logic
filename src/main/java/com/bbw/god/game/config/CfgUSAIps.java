package com.bbw.god.game.config;

import java.util.List;

import lombok.Data;

/**
 * 美国IP地址段
 * 
 * @author suhq
 * @date 2019-08-21 17:39:31
 */
@Data
public class CfgUSAIps implements CfgInterface {
	private String key;
	private List<String> ipRanges;

	@Override
	public String getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 0;
	}
}
