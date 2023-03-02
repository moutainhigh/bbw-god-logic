package com.bbw.god.job.server;

import com.bbw.god.activity.server.ServerActivityGeneratorService;
import com.bbw.god.db.entity.CfgServerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 活动实例生成,每月20号 14:00执行
 *
 * @author suhq
 * @date 2019年3月8日 上午11:10:06
 */
@Component("serverActivityGenerateJob")
public class ServerActivityGenerateJob extends ServerJob {
    // 生成的天数
    private static final int GENERATE_DAYS = 35;
    @Autowired
    private ServerActivityGeneratorService serverActivityGeneratorService;


    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void job(CfgServerEntity server) {
        serverActivityGeneratorService.appendActivities(server, GENERATE_DAYS);
    }

    @Override
    public String getJobDesc() {
        return "区服活动实例";
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
