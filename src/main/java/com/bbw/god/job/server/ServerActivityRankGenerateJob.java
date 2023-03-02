package com.bbw.god.job.server;

import com.bbw.god.activityrank.server.ServerActivityRankGeneratorService;
import com.bbw.god.db.entity.CfgServerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 冲榜实例生成,每月20号 14:00执行
 *
 * @author suhq
 * @date 2019年3月8日 上午11:10:06
 */
@Component("serverActivityRankGenerateJob")
public class ServerActivityRankGenerateJob extends ServerJob {
    // 生成的期数
    private static final int GENERATE_WEEKS = 5;
    @Autowired
    private ServerActivityRankGeneratorService generator;

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void job(CfgServerEntity server) {
        generator.appendActivityRanks(server, GENERATE_WEEKS);
    }

    @Override
    public String getJobDesc() {
        return "区服冲榜实例";
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
