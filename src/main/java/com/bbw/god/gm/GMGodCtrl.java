package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.god.ServerGod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 神仙处理相关接口
 *
 * @author fzj
 * @date 2021/9/29 22:55
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMGodCtrl extends AbstractController {
    @Autowired
    ServerService serverService;

    /**
     * 清楚区服的某个时间后的神仙数据
     * @param serverNames
     * @param sinceDate
     * @return
     */
    @RequestMapping("god!delete")
    public Rst deleteAllServerGod(String serverNames,String sinceDate,int days) {
        Date date = DateUtil.fromDateTimeString(sinceDate);
        int dateInt = DateUtil.toDateInt(date);
        if (dateInt == DateUtil.getTodayInt()){
            return Rst.businessFAIL("不能删除当天的神仙");
        }
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity serverEntity : servers) {
            for (int i = 0; i < days; i++) {
                Date dateToDel = DateUtil.addDays(date,i);
                serverService.deleteServerDatas(serverEntity.getMergeSid(),ServerGod.class,DateUtil.toDateInt(dateToDel)+"");
            }
        }
        return Rst.businessOK();
    }
}