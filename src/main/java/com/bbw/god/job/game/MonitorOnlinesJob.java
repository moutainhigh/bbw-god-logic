package com.bbw.god.job.game;

import com.bbw.god.game.online.GameOnlineService;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监控在线人数
 *
 * @author: suhq
 * @date: 2022/11/25 2:12 下午
 */
@Slf4j
@Component("monitorOnlinesJob")
public class MonitorOnlinesJob extends GameJob {
    @Autowired
    private GameOnlineService gameOnlineService;
    @Autowired
    private MailAction mailAction;

    @Override
    public void job() {
        monitorOnlineNums();
    }

    @Override
    public String getJobDesc() {
        return "用户在线人数监控";
    }

    //必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

    /**
     * 监控在线人数数量
     */
    protected void monitorOnlineNums() {
        int monitorValue = 1000;
        try {
            int onlineNum5 = gameOnlineService.getOnlineNum(5);
            int onlineNum10 = gameOnlineService.getOnlineNum(10);
            boolean isNeedWarnReport = onlineNum5 >= monitorValue && onlineNum10 < monitorValue;
            isNeedWarnReport = isNeedWarnReport || (onlineNum5 - onlineNum10) >= monitorValue;
            if (isNeedWarnReport) {
                mailAction.notifyCoder("在线人数增加监控", "当前在线人数:" + onlineNum5 + "(>" + (onlineNum5 / monitorValue * monitorValue) + ")");
                return;
            }
            boolean isNeedRecoverReport = onlineNum5 < monitorValue && onlineNum10 >= monitorValue;
            isNeedRecoverReport = isNeedRecoverReport || (onlineNum10 - onlineNum5) >= monitorValue;
            if (isNeedRecoverReport) {
                mailAction.notifyCoder("在线人数减少监控", "当前在线人数:" + onlineNum5);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
