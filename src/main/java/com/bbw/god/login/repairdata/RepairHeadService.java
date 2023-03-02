package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_HEAD_DATE;

/**
 * @author suchaobin
 * @description 修复头像service
 * @date 2020/7/7 14:47
 **/
@Service
public class RepairHeadService implements BaseRepairDataService {

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		if (lastLoginDate.before(REPAIR_HEAD_DATE)) {
			gu.repaireHead();
		}
	}
}
