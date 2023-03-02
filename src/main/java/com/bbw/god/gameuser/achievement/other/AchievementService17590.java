package com.bbw.god.gameuser.achievement.other;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.UserCardXianJueService;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌类成就17590service 土之仙诀
 *
 * @author: huanghb
 * @date: 2022/5/4 16:32
 */
@Service
public class AchievementService17590 extends BaseAchievementService {
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
		return 17590;
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
		List<UserCard> cards = userCardService.getUserCards(uid);
		//没有卡牌返回0;
		if (ListUtil.isEmpty(cards)) {
			return 0;
		}
		//对应属性卡牌
		List<UserCard> typeCards = cards.stream().filter(tmp -> TypeEnum.Earth.getValue() == tmp.gainCard().getType()).collect(Collectors.toList());
		if (ListUtil.isEmpty(typeCards)) {
			return 0;
		}
		//获得卡牌id集合
		List<Integer> cardIds = typeCards.stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
		//获得进度
		int count = (int) xianJues.stream().filter(tmp -> cardIds.contains(tmp.getCardId())).count();
		return count;
	}
}
