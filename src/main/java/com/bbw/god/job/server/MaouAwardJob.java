package com.bbw.god.job.server;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 魔王定时发放奖励,已0:10为基准，12:31分，20:31分执行
 *
 * @author suhq
 * @date 2019年3月3日 下午6:43:32
 */
@Component("maouAwardJob")
public class MaouAwardJob extends ServerJob {
    private static byte[] lock = new byte[0];
    @Autowired
    private ServerBossMaouService serverBossMaouService;


    @Override
    public void job(CfgServerEntity server) {
        Date date = DateUtil.now();
        synchronized (lock) {
            this.serverBossMaouService.sendMaouAwards(date, server);
        }
    }

    @Override
    public String getJobDesc() {
        return "魔王奖励发放";
    }

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
