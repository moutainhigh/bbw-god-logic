package com.bbw.god.activity.worldcup.job;

import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.worldcup.WorldCupService;
import com.bbw.god.activity.worldcup.processor.Super16Processor;
import com.bbw.god.job.game.GameJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 玩家超级16强定时发送奖励
 * @author: hzf
 * @create: 2022-11-14 14:15
 **/
@Component("userSuper16AwardsJob")
@Slf4j
public class UserSuper16AwardsJob  extends GameJob {
    @Autowired
    private Super16Processor super16Processor;
    @Override
    public String getJobDesc() {
        return "超级16强开奖";
    }

    @Override
    public void job() {
        super16Processor.sendMailAward();
    }
}
