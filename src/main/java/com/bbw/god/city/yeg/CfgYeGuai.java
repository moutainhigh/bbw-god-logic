package com.bbw.god.city.yeg;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgYeGuai implements CfgInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private Integer ygBoxPrice;
	private List<YeGBoxConfig> yeGBoxs;

	public YeGBoxConfig gainBoxConfig(YeGuaiEnum yeGType, int level, boolean isBusinessGang) {
		YeGBoxConfig yeGBoxConfig = yeGBoxs.stream()
				.filter(tmp -> tmp.getYeGType() == yeGType.getType()
						&& tmp.getIsBusinessGang() == isBusinessGang
						&& tmp.getMinLevel() <= level && tmp.getMaxLevel() >= level)
				.findFirst().orElse(null);
		return yeGBoxConfig;
	}


	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}


	/**
	 * 野怪宝箱配置
	 *
	 * @author: suhq
	 * @date: 2022/2/14 3:19 下午
	 */
	@Data
	public static class YeGBoxConfig {
		private Integer yeGType;
		private Integer minLevel;
		private Integer maxLevel;
		private Boolean isBusinessGang;
		private String boxKey;
	}
}
