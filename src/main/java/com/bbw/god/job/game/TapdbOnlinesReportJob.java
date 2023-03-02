package com.bbw.god.job.game;

import com.bbw.App;
import com.bbw.god.game.online.TapdbReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tapdb用户实时在线上报数据上报
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-29 15:39
 */
@Slf4j
@Component("tapdbOnlinesReportJob")
public class TapdbOnlinesReportJob extends GameJob {
    @Autowired
    private TapdbReporter tapdbReporter;
    @Autowired
    private App app;

    @Override
    public void job() {
        if (app.runAsProd()) {
            tapdbReporter.reportOnline();
        }
    }

    @Override
    public String getJobDesc() {
        return "Tapdb用户实时在线上报数据上报";
    }

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
