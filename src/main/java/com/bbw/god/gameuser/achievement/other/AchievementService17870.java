package com.bbw.god.gameuser.achievement.other;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 秘闻类成就17870service 顶级法器
 *
 * @author: huanghb
 * @date: 2022/5/4 16:32
 */
@Service
public class AchievementService17870 extends BaseAchievementService {
	@Autowired
	private UserCardXianJueService userCardXianJueService;
	@Autowired
	private UserCardService userCardService;

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 17870;
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
		//获得进度
		return 0;
	}
}
