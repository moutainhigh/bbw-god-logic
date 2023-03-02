package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianLogic;
import com.bbw.god.city.mixd.nightmare.NightmareMiXianService;
import com.bbw.god.city.mixd.nightmare.UserNightmareMiXian;
import com.bbw.god.city.mixd.nightmare.pos.CengZhuProcessor;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 梦魇迷仙洞管理接口
 *
 * @author: suhq
 * @date: 2021/11/12 9:08 上午
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMMiXDCtrl {
    @Autowired
    private NightmareMiXianService nightmareMiXianService;
    @Autowired
    private NightmareMiXianLogic nightmareMiXianLogic;
    @Autowired
    private CengZhuProcessor cengZhuProcessor;


    /**
     * 重置玩家的挑战记录到指定层
     *
     * @param uid
     * @return
     */
    @RequestMapping("mxd!setToLevel")
    public Rst setToLevel(long uid, int level) {
        //即放弃挑战 然后初始化下次挑战的层为1
        nightmareMiXianLogic.toGiveUp(uid);
        UserNightmareMiXian nightmareMiXian = nightmareMiXianService.getAndCreateUserNightmareMiXian(uid);
        nightmareMiXian.setNextInitLevel(level);
        nightmareMiXian.setContinuePassLevel(1);
        nightmareMiXianLogic.initNewLevel(nightmareMiXian, nightmareMiXian.getNextInitLevel());
        return Rst.businessOK();
    }

    /**
     * 重置迷仙洞
     *
     * @author: huanghb
     * @date: 2022/6/23 14:52
     */
    @RequestMapping("mxd!reSetCengZhuInfo")
    public Rst reSetMIXDCengZhuINfo() {
        List<Integer> groupIds = ServerTool.getAvailableServers().stream()
                .map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
        for (Integer group : groupIds) {
            cengZhuProcessor.ResetLevelOwnerInfo(group);
        }
        return Rst.businessOK();
    }

}
