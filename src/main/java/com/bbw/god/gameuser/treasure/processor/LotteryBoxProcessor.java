package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureResStatisticService;
import com.bbw.god.gameuser.statistic.resource.treasure.TreasureStatistic;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.random.box.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 奖券超级宝箱处理器
 * @date 2020/7/7 11:02
 */
@Service
public class LotteryBoxProcessor extends TreasureUseProcessor {
	@Autowired
	private TreasureResStatisticService statisticService;
	@Autowired
	private BoxService boxService;
	@Autowired
	private UserCardService userCardService;

	public LotteryBoxProcessor() {
		this.treasureEnum = TreasureEnum.LOTTERY_BOX;
		this.isAutoBuy = false;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	@Override
	public boolean isChestType() {
		return true;
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		long uid = gu.getId();
		int treasureId = this.treasureEnum.getValue();
		TreasureStatistic statistic = statisticService.fromRedis(uid, StatisticTypeEnum.CONSUME,
				DateUtil.getTodayInt());
		int useTimes = statistic.getTotalNum(TreasureTool.getTreasureById(treasureId));
		// 金睛白虎
		UserCard userCard = userCardService.getUserCard(uid, 142);
		// 已经开了2次且都没有获得金睛白虎，这次必得金睛白虎
		if (2 == useTimes && null == userCard) {
			CardEventPublisher.pubCardAddEvent(uid, 142, WayEnum.LOTTERY_BOX, "", rd);
			return;
		}
		boxService.open(gu.getId(), treasureId, WayEnum.LOTTERY_BOX, rd);
	}
}
