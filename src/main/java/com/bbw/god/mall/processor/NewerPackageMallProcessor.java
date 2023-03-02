package com.bbw.god.mall.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityStatusEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 新手礼包
 * @date 2020/7/15 17:31
 **/
@Service
public class NewerPackageMallProcessor extends FavorableMallProcessor {
	@Autowired
	private ActivityService activityService;
	@Autowired
	private UserTreasureService userTreasureService;

	NewerPackageMallProcessor() {
		this.mallType = MallEnum.NEWER_PACKAGE;
	}

	@Override
	public RDMallList getGoods(long guId) {
		List<CfgMallEntity> fMalls = MallTool.getMallConfig().getNewerPackageMalls();
		RDMallList rd = new RDMallList();
		toRdMallList(guId, fMalls, false, rd);
		return rd;
	}

	/**
	 * 检查权限
	 *
	 * @param uid
	 * @param mall
	 */
	@Override
	public void checkAuth(long uid, CfgMallEntity mall) {
		if (1221 == mall.getId()) {
			int num = userTreasureService.getTreasureNum(uid, TreasureEnum.GOD_VOUCHER.getValue());
			if (num < 1) {
				throw new ExceptionForClientTip("treasure.not.enough", TreasureEnum.GOD_VOUCHER.getName());
			}
		}
	}

	@Override
	public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
		WayEnum way = WayEnum.OPEN_ShangXLB;
		TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.FIVE_STAR_SUMMON_SYMBOL.getValue(), 1, way, rd);
		TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.QKT.getValue(), 1, way, rd);
		TreasureEventPublisher.pubTAddEvent(guId, TreasureEnum.SS.getValue(), 15, way, rd);
		ResEventPublisher.pubCopperAddEvent(guId, 200000, way, rd);
		ResEventPublisher.pubDiceAddEvent(guId, 180, way, rd);
		if (1221 == mall.getId()) {
			TreasureEventPublisher.pubTDeductEvent(guId, TreasureEnum.GOD_VOUCHER.getValue(), 1, way, rd);
		}
		CfgActivityEntity ca = ActivityTool.getActivitiesByType(ActivityEnum.NEWER_PACKAGE).stream()
				.filter(a -> a.getId().equals(10055)).findFirst().orElse(null);
		int sid = gameUserService.getActiveSid(guId);
		Long aId = activityService.getActivity(sid, ActivityEnum.NEWER_PACKAGE).gainId();
		UserActivity userActivity = activityService.getUserActivity(guId, aId, ca.getId());
		if (null == userActivity) {
			userActivity = UserActivity.fromActivity(guId, aId, 0, ca);
			gameUserService.addItem(guId, userActivity);
		}
		userActivity.setStatus(ActivityStatusEnum.AWARDED0.getValue());
		gameUserService.updateItem(userActivity);
	}
}
