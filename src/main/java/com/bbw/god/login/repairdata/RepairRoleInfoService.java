package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author suchaobin
 * @description 修复uid = null的问题
 * @date 2020/7/7 15:20
 **/
@Service
public class RepairRoleInfoService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsRoleInfoService insRoleInfoService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        //修复uid = null的问题
        if (gu.getRoleInfo().getUid() == null) {
            gu.getRoleInfo().setUid(gu.getId());
            gu.updateRoleInfo();
            gu.getStatus().setUid(gu.getId());
            gu.updateStatus();
            gu.getSetting().setUid(gu.getId());
            gu.updateSetting();
        }
        if (null == gu.getRoleInfo().getRegTime()) {
            UserLoginInfo userLoginInfo = gameUserService.getSingleItem(gu.getId(), UserLoginInfo.class);
            gu.getRoleInfo().setRegTime(userLoginInfo.getEnrollTime());
            gu.updateRoleInfo();
        }
        if (null == gu.getRoleInfo().getRegTime() || null == gu.getRoleInfo().getUserName() || null == gu.getRoleInfo().getNickname()) {
            InsRoleInfoEntity roleInfoEntity = insRoleInfoService.selectById(gu.getId());
            gu.getRoleInfo().setUserName(roleInfoEntity.getUsername());
            gu.getRoleInfo().setNickname(roleInfoEntity.getNickname());
            if (null == gu.getRoleInfo().getRegTime()) {
                gu.getRoleInfo().setRegTime(DateUtil.fromDateInt(roleInfoEntity.getRegDate()));
            }
            gu.updateRoleInfo();
        }
    }
}
