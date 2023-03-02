package com.bbw.god.gameuser.achievement;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就配置类，对应 成就配置.yml
 * @date 2020/2/19 8:52
 */
@Data
public class CfgAchievement implements CfgInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private String key;
	private Integer recentNumToShow;
	private Integer allScore;
	private List<CfgAchievementTypeInfo> typeInfos;

	@Data
	public static class CfgAchievementTypeInfo {
		private Integer id;
		private String name;
		private Integer num;
		private Integer totalScore;
	}

	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}
}
