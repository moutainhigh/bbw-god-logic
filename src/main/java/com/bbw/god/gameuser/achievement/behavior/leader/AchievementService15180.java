package com.bbw.god.gameuser.achievement.behavior.leader;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** 超凡脱俗
 * @author lzc
 * @date 2021/4/13 10:01
 **/
@Service
public class AchievementService15180 extends BaseAchievementService {
	@Autowired
	private LeaderCardService leaderCardService;
	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 15180;
	}

	/**
	 * 获取当前成就进度(用于展示给客户端)
	 *
	 * @param uid  玩家id
	 * @param info 成就对象信息
	 * @return 当前成就进度
	 */
	@Override
	public int getMyProgress(long uid, UserAchievementInfo info) {
		Optional<UserLeaderCard> optional = leaderCardService.getUserLeaderCardOp(uid);
		if (optional.isPresent()){
			return optional.get().getHv();
		}
		return 0;
	}
}
