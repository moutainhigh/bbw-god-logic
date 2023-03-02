package com.bbw.god.job.game;

import com.bbw.god.activity.game.GameActivityGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 全服活动实例生成
 *
 * @author suhq
 * @date 2020-07-23 10:23
 **/
@Slf4j
@Component("gameActivityGenerateJob")
public class GameActivityGenerateJob extends GameJob {
    // 生成的天数
    private static final int GENERATE_DAYS = 35;
    @Autowired
    private GameActivityGeneratorService gameActivityGeneratorService;

    @Override
    public void job() {
        gameActivityGeneratorService.appendActivities(GENERATE_DAYS);
    }

    @Override
    public String getJobDesc() {
        return "全服活动实例";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
