package com.bbw.god.login.repairdata;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.game.config.CfgWishCard;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.cardshop.CardPoolEnum;
import com.bbw.god.mall.cardshop.CardShopService;
import com.bbw.god.mall.cardshop.LimitTimePoolDrawService;
import com.bbw.god.mall.cardshop.UserCardPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.*;

/**
 * @author suchaobin
 * @description 重置卡池
 * @date 2020/7/7 14:56
 **/
@Service
public class ResetCardShopService implements BaseRepairDataService {
	@Autowired
	private CardShopService cardShopService;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private LimitTimePoolDrawService limitTimePoolDrawService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		if (lastLoginDate.before(RESET_CARD_SHOP_DATE)) {
			//重置卡池，本次只重置解锁状态和解锁时间
			this.cardShopService.resetUserCardPool(gu.getId());
		}
		int sid = gameUserService.getActiveSid(gu.getId());
		// 限时卡池活动是否开启
		boolean isValidActivity = limitTimePoolDrawService.isValidActivity(sid);
		if (isValidActivity && lastLoginDate.before(RESET_CARD_SHOP_WISH)) {
			//重置限时卡池许愿值
			cardShopService.resetCardPoolWish(gu.getId());
		}
		if (!lastLoginDate.before(RESET_LIMIT_CARD_SHOP)) {
			return;
		}
		if (!isValidActivity) {
			return;
		}
		resetLimitCardsPool(gu.getId());

	}

	/**
	 * 重置限时卡池（重置限时卡池许愿卡）
	 *
	 * @param uid
	 */
	public void resetLimitCardsPool(long uid) {
		//获得玩家限时卡池
		List<UserCardPool> ucPools = cardShopService.getCardPoolRecords(uid);
		UserCardPool limitTimeCardPool = ucPools.stream().filter(tmp -> tmp.getCardPool() == CardPoolEnum.LIMIT_TIME_CP.getValue()).findFirst().orElse(null);
		//卡池为空
		if (null == limitTimeCardPool) {
			return;
		}

		List<CfgWishCard.WishCard> wishCards = cardShopService.getWishCards(uid, CardPoolEnum.LIMIT_TIME_CP.getValue());
		CfgWishCard.WishCard wishCard = wishCards.get(0);
		limitTimeCardPool.setWishCard(wishCard.getId());
		limitTimeCardPool.setNeedWish(wishCard.getNeedWish());
		gameUserService.updateItems(ucPools);

	}
}
