package com.bbw.god.login.repairdata;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gm.UserGmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 重置每日任务
 * @date 2020/7/7 14:53
 **/
@Service
public class RepairDailyTaskService implements BaseRepairDataService {
    @Autowired
    private UserGmService userGmService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
    }
}
