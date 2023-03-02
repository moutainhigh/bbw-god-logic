package com.bbw.god.gameuser.achievement;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.Cfg;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AchievementTool {

	public static CfgAchievementEntity getAchievement(int achievementId) {
		CfgAchievementEntity entity = Cfg.I.get(achievementId, CfgAchievementEntity.class);
		if (entity == null) {
			throw CoderException.high("成就" + achievementId + "不存在");
		}
		return entity;
	}

	public static List<CfgAchievementEntity> getSerialAchievements(AchievementSerialEnum serial) {
		return getAllAchievements().stream().filter(a -> a.getSerial() == serial.getValue()).collect(Collectors.toList());
	}

	public static List<CfgAchievementEntity> getAllAchievements() {
		return Cfg.I.get(CfgAchievementEntity.class).stream().filter(CfgAchievementEntity::getIsValid).collect(Collectors.toList());
	}

	public static List<CfgAchievementEntity> getAchievements(AchievementTypeEnum type) {
		return getAllAchievements().stream().filter(tmp -> tmp.getType() == type.getValue() && tmp.getIsValid())
				.sorted(Comparator.comparing(CfgAchievementEntity::getOrder)).collect(Collectors.toList());
	}

	public static CfgAchievement getCfgAchievement() {
		return Cfg.I.getUniqueConfig(CfgAchievement.class);
	}

	public static List<CfgAchievementEntity> getInvalidAchievements() {
		return Cfg.I.get(CfgAchievementEntity.class).stream().filter(a -> !a.getIsValid()).collect(Collectors.toList());
	}
}
