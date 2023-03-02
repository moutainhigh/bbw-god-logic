package com.bbw.god.mall.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;

/**
 * 商城未显示但是可购买的道具
 * 
 * @author suhq
 * @date 2019年2月26日 上午11:54:27
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotShowMallProcessor extends AbstractMallProcessor {
	@Autowired
	private ActivityService activityService;

	NotShowMallProcessor() {
		this.mallType = MallEnum.NOT_SHOWED;
	}

	@Override
	public RDMallList getGoods(long guId) {
		int sId = gameUserService.getActiveSid(guId);
		boolean isDiscount = activityService.isActive(sId, ActivityEnum.MALL_DISCOUNT);
		RDMallList rd = new RDMallList();
		toRdMallList(guId, MallTool.getMallConfig().getNotShowMalls(), isDiscount, rd);
		return rd;
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		TreasureEventPublisher.pubTAddEvent(guId, mall.getGoodsId(), buyNum, WayEnum.MALL_BUY, rd);
	}

	@Override
	protected List<UserMallRecord> getUserMallRecords(long guId) {
		return null;
	}

}
