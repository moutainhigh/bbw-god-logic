package com.bbw.god.gameuser.achievement.resource;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 资源成就基础service
 * @date 2020/5/14 11:10
 **/
@Service
public abstract class ResourceAchievementService extends BaseAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	public abstract AwardEnum getMyAwardEnum();
}
