package com.bbw.god.job.game;

import com.bbw.App;
import com.bbw.god.game.online.RealNameOnlineBehaviorReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实名用户上下线行为上报
 *
 * @author suhq
 * @date 2021-05-19 08:47
 **/
@Component("realNameOnlineBehaviorReportJob")
public class RealNameOnlineBehaviorReportJob extends GameJob {
    @Autowired
    private RealNameOnlineBehaviorReporter realNameOnlineBehaviorReporter;
    @Autowired
    private App app;

    @Override
    public void job() {
//		if (app.runAsProd()) {
        realNameOnlineBehaviorReporter.reportOnline();
//		}
    }

    @Override
    public String getJobDesc() {
        return "实名用户上下线行为上报";
    }

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
