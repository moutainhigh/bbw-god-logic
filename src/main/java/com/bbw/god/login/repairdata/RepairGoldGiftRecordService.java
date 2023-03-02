package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.RESET_GOLD_GIFT_RECORD;

/**
 * 重置元宝礼包记录
 *
 * @author: huanghb
 * @date: 2022/6/20 15:21
 */
@Service
public class RepairGoldGiftRecordService implements BaseRepairDataService {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private MallService mallService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		boolean isResetTimes = lastLoginDate.before(RESET_GOLD_GIFT_RECORD) && DateUtil.now().after(RESET_GOLD_GIFT_RECORD);
		if (isResetTimes) {
			// 获得元宝礼包记录
			List<UserMallRecord> mallRecords = mallService.getUserMallRecord(gu.getId(), MallEnum.GOLD_RECHARGE_BAG);
			// 重置礼包记录
			if (ListUtil.isNotEmpty(mallRecords)) {
				this.gameUserService.deleteItems(gu.getId(), mallRecords);
			}
		}
	}
}
