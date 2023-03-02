package com.bbw.god.login.repairdata;

import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.FA_TAN_STATISTIC_REPAIR;

/**
 * 法坛统计数据修复
 *
 * @author fzj
 * @date 2022/3/3 9:24
 */
@Service
public class repairFaTanStatisticDataService implements BaseRepairDataService {
    @Autowired
    FaTanService faTanService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (!lastLoginDate.before(FA_TAN_STATISTIC_REPAIR)) {
           return;
        }
        this.faTanService.repairFaTanStatistic(gu.getId());
    }
}
