package com.bbw.god.login.repairdata;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.*;

/**
 * @author suchaobin
 * @description 修复/重置活动相关数据
 * @date 2020/7/7 14:58
 **/
@Service
public class ResetActivityService implements BaseRepairDataService {
	@Autowired
	private ActivityService activityService;
	@Autowired
	private MallService mallService;
	@Autowired
	private GameUserService gameUserService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		// 修复进度，当前仅支持今日充值、累计充值
		if (lastLoginDate.before(REPAIR_RMB_PROGRESS_DATE)) {
			this.activityService.repaireToRMBProgress(gu.getId());
		}
		// 重置七日之约，并将今日置为可领取
		if (lastLoginDate.before(RESET_SEVEN_LOGIN_DATE)) {
			this.activityService.resetSevenLogin(gu.getServerId(), gu.getId());
		}
		// 重置周礼包
		if (lastLoginDate.before(RESET_WEEK_BAG_DATE)) {
			List<UserMallRecord> weekRecords = this.mallService.getUserMallRecord(gu.getId(),
					MallEnum.WEEK_RECHARGE_BAG);
			this.gameUserService.deleteItems(gu.getId(), weekRecords);
		}
		// 重置月礼包
		if (lastLoginDate.before(RESET_MONTH_BAG_DATE)) {
			List<UserMallRecord> monthRecords = this.mallService.getUserMallRecord(gu.getId(),
					MallEnum.MONTH_RECHARGE_BAG);
			this.gameUserService.deleteItems(gu.getId(), monthRecords);
		}
	}
}
