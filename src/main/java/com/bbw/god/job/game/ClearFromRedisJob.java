package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.clear.RemoveUnloginUserFromRedis;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * redis缓存释放
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-29 15:39
 */
@Slf4j
@Component("clearFromRedisJob")
public class ClearFromRedisJob extends GameJob {
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private ServerUserService userService;
    @Autowired
    private RemoveUnloginUserFromRedis removeUnloginUserFromRedis;
    @Value("${bbw-god.redis-userdata-in-days:10}")
    private int cacheDays;// redis中缓存的多少天内登录的用户数据

    @Override
    public void job() {
        long begin = System.currentTimeMillis();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        Date beforeDate = DateUtil.addDays(DateUtil.now(), -(cacheDays + 3));
        Date endDate = DateUtil.addDays(DateUtil.now(), -cacheDays);
        long total = 0;
        for (CfgServerEntity server : servers) {
            // cacheDays天内登录的用户
            List<Long> uids = roleInfo.getUidsLoginBetween(server.getId(), DateUtil.toDateInt(beforeDate), DateUtil.toDateInt(endDate));
            if (ListUtil.isEmpty(uids)) {
                continue;
            }
            userService.unloadGameUsers(uids, server.getName());
        }
        // 4天内，2天前有登录的等级<=4的用户
        beforeDate = DateUtil.addDays(DateUtil.now(), -4);
        endDate = DateUtil.addDays(DateUtil.now(), -2);
        List<Long> uids = roleInfo.getUidsLevelLess4LoginBetween(DateUtil.toDateInt(beforeDate), DateUtil.toDateInt(endDate));
        if (null != uids) {
            log.info("移除2天前等级<=4的无效账号{}个!", uids.size());
            userService.unloadGameUsers(uids, "未指定");
        }
        removeUnloginUserFromRedis.clear();
        long end = System.currentTimeMillis();
        log.info("移除{}个redis键值耗时:{}.", total, (end - begin));
    }

    @Override
    public String getJobDesc() {
        return "redis缓存释放";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
