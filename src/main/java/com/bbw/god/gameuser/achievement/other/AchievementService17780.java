package com.bbw.god.gameuser.achievement.other;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.CfgXianJueTool;
import com.bbw.god.gameuser.card.equipment.Enum.CardEquipmentAdditionEnum;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 卡牌类成就17780service 低唱浅斟I
 *
 * @author: huanghb
 * @date: 2022/5/4 16:32
 */
@Service
public class AchievementService17780 extends BaseAchievementService {
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
		return 17780;
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
		List<UserCardXianJue> xianJues = userCardXianJueService.getUserCardXianJues(uid);
		//获得进度
		return (int) xianJues.stream()
				.filter(tmp -> CfgXianJueTool.getComprehendLimitInfo()
						.equals(tmp.gainCurrenAddition(CardEquipmentAdditionEnum.TENACITY_RATE.getValue())))
				.count();
	}
}
