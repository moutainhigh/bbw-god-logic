package com.bbw.god.mall.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.statistics.userstatistic.ActionStatisticTool;

/**
 * 常规道具
 * 
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TreasureMallProcessor extends AbstractMallProcessor {
	@Autowired
	private ActivityService activityService;
	@Autowired
	private BoxService boxService;

	TreasureMallProcessor() {
		this.mallType = MallEnum.DJ;
	}

	@Override
	public RDMallList getGoods(long guId) {
		int sId = gameUserService.getActiveSid(guId);
		boolean isDiscount = activityService.isActive(sId, ActivityEnum.MALL_DISCOUNT);
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getTreasureMalls(), isDiscount, rd);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		int proId = mall.getGoodsId();
		if ((proId == TreasureEnum.BX.getValue() || proId == TreasureEnum.BBX.getValue()) && buyNum > 1) {
			throw new ExceptionForClientTip("mall.onlyOne");
		}
		if (proId == TreasureEnum.BX.getValue()) {
			boxService.open(guId, mall.getGoodsId(), WayEnum.OPEN_BaoX, rd);
			ActionStatisticTool.addUserActionStatistic(guId, 1, WayEnum.OPEN_BaoX.getName());
		} else if (proId == TreasureEnum.BBX.getValue()) {
			boxService.open(guId, mall.getGoodsId(), WayEnum.OPEN_BaiBX, rd);
		} else {// 购买宝物
			TreasureEventPublisher.pubTAddEvent(guId, proId, buyNum, WayEnum.MALL_BUY, rd);
		}
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		return null;
	}

}
