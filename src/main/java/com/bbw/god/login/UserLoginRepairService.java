package com.bbw.god.login;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.login.repairdata.BaseRepairDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 玩家登陆数据修复
 */
@Slf4j
@Service
public class UserLoginRepairService {
    // private static final String CACHE_TYPE = "loginUid";
    @Autowired
    private GameUserService userService;
    @Autowired
    @Lazy
    private List<BaseRepairDataService> repairDataServiceList;

    /**
     * 登录修复数据
     *
     * @param gu
     */
    public void repairAsLogin(GameUser gu) {
        UserLoginInfo uLoginInfo = this.userService.getSingleItem(gu.getId(), UserLoginInfo.class);
        if (uLoginInfo == null) {
            return;
        }
        Date lastLoginDate = uLoginInfo.getLastLoginTime();
        for (BaseRepairDataService repairService : repairDataServiceList) {
            repairService.repair(gu, lastLoginDate);
        }
    }

}
