package com.bbw.god.gameuser.achievement;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就总入口
 * @date 2020/2/20 14:05
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDAchievementInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private CfgAchievement cfgAchievement;
	private RDUserAchievementInfo userAchievementInfo;
	private List<RDAchievementList.RDAchievement> recentAchievements;

	public static RDAchievementInfo getInstance(RDUserAchievementInfo userAchievementInfo, List<RDAchievementList.RDAchievement> recentAchievements) {
		RDAchievementInfo rdAchievementInfo = new RDAchievementInfo();
		rdAchievementInfo.setUserAchievementInfo(userAchievementInfo);
		rdAchievementInfo.setRecentAchievements(recentAchievements);
		rdAchievementInfo.setCfgAchievement(statisticsInfo());
		return rdAchievementInfo;
	}

    /**
     * 成就信息统计
     *
     * @return
     */
	private static CfgAchievement statisticsInfo() {
		CfgAchievement cfgAchievement = AchievementTool.getCfgAchievement();
		int allScore = 0;
		List<CfgAchievement.CfgAchievementTypeInfo> typeInfos = cfgAchievement.getTypeInfos();
		for (CfgAchievement.CfgAchievementTypeInfo typeInfo : typeInfos) {
			int totalScore = 0;
			int num = 0;
            AchievementTypeEnum typeEnum = AchievementTypeEnum.fromValue(typeInfo.getId());
            List<CfgAchievementEntity> achievementEntities = AchievementTool.getAchievements(typeEnum);
			int score = achievementEntities.stream().mapToInt(CfgAchievementEntity::getScore).sum();
			allScore += score;
			totalScore += score;
			num += achievementEntities.size();
			typeInfo.setNum(num);
			typeInfo.setTotalScore(totalScore);
		}
		cfgAchievement.setAllScore(allScore);
		return cfgAchievement;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@NoArgsConstructor
	public static class RDUserAchievementInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<RDAchievementFinishInfo> finishInfoList = new ArrayList<>();
		private Integer totalScore = 0;

		public void addFinishInfo(Integer type, Integer finishNum, boolean ableAward) {
			this.finishInfoList.add(new RDAchievementFinishInfo(type, finishNum, ableAward));
		}

		public void addScore(int score) {
			this.totalScore += score;
		}
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@AllArgsConstructor
	public static class RDAchievementFinishInfo implements Serializable {
		private static final long serialVersionUID = 1375801063786139309L;
		private Integer type;
		private Integer finishNum;
		private boolean ableAward;
	}
}
