package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocInfoService;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 升级商会版本
 * @date 2020/7/7 15:02
 **/
@Service
public class RepairCocService implements BaseRepairDataService {
    @Autowired
    private UserCocInfoService userCocInfoService;
    @Autowired
    private UserCocTaskService userCocTaskService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
//		if (lastLoginDate.before(RESET_COC_DATE)) {
//			userCocInfoService.updateCoc(gu.getId());
//		}
//		if (lastLoginDate.before(RESET_COC_TASK_DATE)) {
//			userCocTaskService.repair(gu.getId());
//		}
    }
}
