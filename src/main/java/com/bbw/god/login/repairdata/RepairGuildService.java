package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.guild.UserGuildTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 修复行会相关数据
 *
 * @author suhq
 * @date 2020-08-27 15:04
 **/
@Slf4j
@Service
public class RepairGuildService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        int todayInt = DateUtil.getTodayInt();
        // 修复重复的行会任务数据
        List<UserGuildTaskInfo> userGuildTaskInfos = gameUserService.getMultiItems(gu.getId(), UserGuildTaskInfo.class);
        if (userGuildTaskInfos.size() > 1) {
            log.error("{}UserGuildTaskInfo出现重复，数据条数：{}", gu.getRoleInfo().getNickname(), userGuildTaskInfos.size());
            List<UserGuildTaskInfo> toDels = userGuildTaskInfos.stream().filter(tmp -> tmp.getBuildDate() < todayInt).collect(Collectors.toList());
            if (ListUtil.isNotEmpty(toDels)) {
                gameUserService.deleteItems(gu.getId(), toDels);
            }
        }
    }
}
