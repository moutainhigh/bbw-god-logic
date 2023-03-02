package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.util.List;

/**
 * 城池相关配置
 *
 * @author suhq
 * @date 2019-05-05 18:07:39
 */
@Data
public class CfgChengC implements CfgInterface {
	private String key;
	private List<ChengC> chengCs;// 城池列表
	@Override
	public String getId() {
		return this.key;
	}

	@Override
	public int getSortId() {
		return 1;
	}

}
