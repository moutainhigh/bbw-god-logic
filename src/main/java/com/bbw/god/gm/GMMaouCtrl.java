package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouDayConfigService;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouDayConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 魔王相关接口
 *
 * @author suhq
 * @date 2019年4月12日 上午11:55:43
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMMaouCtrl extends AbstractController {
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerBossMaouDayConfigService serverBossMaouDayConfigService;
    @Autowired
    private ServerAloneMaouDayConfigService serverAloneMaouDayConfigService;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;

    /**
     * 重新生成魔王
     *
     * @param serverNames
     * @return
     */
    @GetMapping("server!regenerateBossMaou")
    public Rst regenerageBossMaou(String serverNames, String sinceDate, int days) {
        Date baseDate = DateUtil.fromDateTimeString(sinceDate);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            try {
                int sid = server.getMergeSid();
                for (int i = 0; i < days; i++) {
                    Date regenerateDate = DateUtil.addDays(baseDate, i);
                    List<ServerBossMaou> sbms = this.serverDataService.getServerDatas(sid, ServerBossMaou.class,
                            DateUtil.toDateInt(regenerateDate) + "");
                    List<Long> delIds =
                            sbms.stream().filter(tmp -> tmp.getBeginTime().after(baseDate)).map(ServerBossMaou::getId).collect(Collectors.toList());
                    this.serverDataService.deleteServerDatas(sid, delIds, ServerBossMaou.class);
                    this.serverBossMaouDayConfigService.check(server, regenerateDate);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return Rst.businessOK();
    }

    @GetMapping("server!regenerateAloneMaou")
    public Rst regenerateAloneMaou(String serverNames, String sinceDate, int days) {
        Date baseDate = DateUtil.fromDateTimeString(sinceDate);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        for (CfgServerEntity server : servers) {
            try {
                int sid = server.getMergeSid();
                for (int i = 0; i < days; i++) {
                    Date regenerateDate = DateUtil.addDays(baseDate, i);
                    List<ServerAloneMaou> sbms = this.serverDataService.getServerDatas(sid, ServerAloneMaou.class,
                            DateUtil.toDateInt(regenerateDate) + "");
                    List<Long> delIds =
                            sbms.stream().filter(tmp -> tmp.getBeginTime().after(baseDate)).map(ServerAloneMaou::getId).collect(Collectors.toList());
                    this.serverDataService.deleteServerDatas(sid, delIds, ServerAloneMaou.class);
                    this.serverAloneMaouDayConfigService.check(server, regenerateDate);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return Rst.businessOK();
    }

    @GetMapping("server!cleanAloneMaouAttackSummary")
    public Rst cleanAloneMaouAttackSummary(String serverNames, String sinceDate, String endDate) {
        Date begin = DateUtil.fromDateTimeString(sinceDate);
        Date end = DateUtil.fromDateTimeString(endDate);
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        List<String> keys = new ArrayList<>();
        for (CfgServerEntity server : servers) {
            int sid = server.getMergeSid();
            int beginDateInt = DateUtil.toDateInt(begin);
            int endDateInt = DateUtil.toDateInt(end);
            List<Long> uids = insRoleInfoService.getUidsLoginBetween(sid, beginDateInt, endDateInt).stream().distinct().collect(Collectors.toList());
            int daysBetween = DateUtil.getDaysBetween(begin, end);
            for (int i = 0; i <= daysBetween; i++) {
                int dateInt = DateUtil.toDateInt(DateUtil.addDays(begin, i));
                // 今日的记录不删除
                if (dateInt == DateUtil.getTodayInt()) {
                    continue;
                }
                String dateKey = sid + String.valueOf(dateInt).substring(4) + "61000";
                for (Long uid : uids) {
                    String key = "server" + SPLIT + sid + SPLIT + "maouAlone" + SPLIT + dateKey + SPLIT + "maouLevelInfo" + SPLIT + uid;
                    keys.add(key);
                }
            }
        }
        redisHashUtil.delete(keys);
        return Rst.businessOK();
    }
}
