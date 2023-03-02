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
 * @description 成就id=13300的service
 * @date 2020/5/14 17:54
 **/
@Service
public class AchievementService13300 extends BaseAchievementService {
	@Autowired
	private UserCardService userCardService;

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13300;
	}

	/**
	 * 获取当前成就进度
	 *
	 * @param uid  玩家id
	 * @param info 成就对象信息
	 * @return 当前成就进度
	 */
	@Override
	public int getMyProgress(long uid, UserAchievementInfo info) {
		if (isAccomplished(info)) {
			return getMyNeedValue();
		}
		List<UserCard> userCards = userCardService.getUserCards(uid);
		long count = userCards.stream().filter(uc ->
				Arrays.asList(102, 202, 301, 402, 501, 10102).contains(CardTool.getNormalCardId(uc.getBaseId()))).count();
		return (int) count;
	}
}
