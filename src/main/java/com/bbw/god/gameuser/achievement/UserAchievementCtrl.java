package com.bbw.god.gameuser.achievement;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserAchievementCtrl extends AbstractController {
	@Autowired
	private UserAchievementLogic achievementLogic;

	@GetMapping(CR.Achievement.LIST_ACHIEVEMENT)
	public RDAchievementList listAchievement(Integer type) {
		return achievementLogic.listAchievement(getUserId(), AchievementTypeEnum.fromValue(type));
	}

	@GetMapping(CR.Achievement.LIST_ACHIEVEMENT_V2)
	public RDAchievementList listAchievementV2(Integer type) {
		RDAchievementList rdAchievementList = achievementLogic.listAchievement(getUserId(), AchievementTypeEnum.fromValue(type));
		List<RDAchievementList.RDAchievement> achievements = rdAchievementList.getAchievements();
		List<RDAchievementItem> items = achievements.stream().map(rd ->
				new RDAchievementItem(rd.getId(), rd.getStatus(), rd.getProgress(), rd.getType()))
				.collect(Collectors.toList());
		rdAchievementList.setItems(items);
		rdAchievementList.setAchievements(null);
		return rdAchievementList;
	}

	@GetMapping(CR.Achievement.GAIN_AWARD)
	public RDCommon getAwards(int id) {
		return achievementLogic.getAchievementAward(getUserId(), id);
	}

	@GetMapping(CR.Achievement.ACHIEVEMENT_INFO)
	public RDAchievementInfo getAchievementInfo() {
		return achievementLogic.getAchievementInfo(getUserId());
	}

	@GetMapping(CR.Achievement.ACHIEVEMENT_GAME_RANK)
	public RDAchievementRankInfo getGameAchievementRank(int start, int end) {
		return achievementLogic.getGameRDAchievementRankInfo(getUserId(), start, end);
	}

	@GetMapping(CR.Achievement.ACHIEVEMENT_SERVER_RANK)
	public RDAchievementRankInfo getServerAchievementRank(Integer sid, int start, int end) {
		return achievementLogic.getServerRDAchievementRankInfo(getUserId(), sid, start, end);
	}
}
