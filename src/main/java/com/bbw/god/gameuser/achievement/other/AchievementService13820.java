package com.bbw.god.gameuser.achievement.other;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.special.UserSpecialSaleRecord;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 成就id=13820的service
 * @date 2020/5/25 15:24
 **/
@Service
public class AchievementService13820 extends ResourceAchievementService {
	/**
	 * 获取当前资源类型
	 *
	 * @return 当前资源类型
	 */
	@Override
	public AwardEnum getMyAwardEnum() {
		return AwardEnum.TC;
	}

	/**
	 * 获取当前成就id
	 *
	 * @return 当前成就id
	 */
	@Override
	public int getMyAchievementId() {
		return 13820;
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
		UserSpecialSaleRecord record = gameUserService.getSingleItem(uid, UserSpecialSaleRecord.class);
		if (null == record) {
			return 0;
		}
		return record.getSaledSpecialList().size();
	}
}
