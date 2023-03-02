package com.bbw.god.gameuser.achievement.other;

import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就id=14730的service
 * @date 2020/6/18 14:25
 **/
@Service
public class AchievementService14730 extends BaseAchievementService {
	private static final List<Integer> GSLX_CARDS = Arrays.asList(542, 441, 440, 346, 245, 141);
	@Autowired
	private UserCardService userCardService;

	@Override
	public int getMyAchievementId() {
		return 14730;
	}

	@Override
	public int getMyProgress(long uid, UserAchievementInfo info) {
		List<UserCard> userCards = userCardService.getUserCards(uid);
		long count = userCards.stream().filter(uc -> GSLX_CARDS.contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
		return (int) count;
	}
}
