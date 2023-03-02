package com.bbw.god.login.repairdata;

import com.bbw.god.activity.processor.MultipleRebateProcessor;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_MULTIPLE_REBATE_DATE;

/**
 * @author suchaobin
 * @description 3倍返利更新后进度修复
 * @date 2020/7/7 14:52
 **/
@Service
public class RepairMultiRebebateService implements BaseRepairDataService {
	@Autowired
	private MultipleRebateProcessor multipleRebateProcessor;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		// 3倍返利更新后进度修复
		if (lastLoginDate.before(REPAIR_MULTIPLE_REBATE_DATE)) {
			this.multipleRebateProcessor.repaireProgress(gu.getId());
		}
	}
}
