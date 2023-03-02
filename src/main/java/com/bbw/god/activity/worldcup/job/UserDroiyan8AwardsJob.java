package com.bbw.god.activity.worldcup.job;

import com.bbw.god.activity.worldcup.processor.Droiyan8Processor;
import com.bbw.god.job.game.GameJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 决战8强 定时开奖
 * @author: hzf
 * @create: 2022-11-21 16:44
 **/
@Component("userDroiyan8AwardsJob")
@Slf4j
public class UserDroiyan8AwardsJob extends GameJob {

    @Autowired
    private Droiyan8Processor droiyan8Processor;
    @Override
    public String getJobDesc() {
        return "决战8强开奖";
    }

    @Override
    public void job() {
        droiyan8Processor.sendMailAward();
    }
}
