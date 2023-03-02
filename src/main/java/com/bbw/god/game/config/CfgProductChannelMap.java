package com.bbw.god.game.config;

import java.util.List;

import lombok.Data;

/**渠道产品ID与竹风产品ID映射
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-06 16:19
 */
@Data
public class CfgProductChannelMap implements CfgInterface {
	private int channelId;//客户端渠道编码
	private List<ChannelMap> channelMap;

	@Data
	public static class ChannelMap {
		private int zfProductId;//竹风产品ID
		private String innerId;//渠道产品ID
	}

	@Override
	public Integer getId() {
		return channelId;
	}

	@Override
	public int getSortId() {
		return channelId;
	}
}
