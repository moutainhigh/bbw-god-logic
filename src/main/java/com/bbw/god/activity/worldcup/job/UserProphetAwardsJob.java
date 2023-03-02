package com.bbw.god.activity.worldcup.job;

import com.bbw.god.activity.worldcup.processor.ProphetProcessor;
import com.bbw.god.job.game.GameJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 我是预言家定时奖励发送
 * @author: hzf
 * @create: 2022-11-21 16:44
 **/
@Component("userProphetAwardsJob")
@Slf4j
public class UserProphetAwardsJob  extends GameJob {

    @Autowired
    private ProphetProcessor prophetProcessor;

    @Override
    public String getJobDesc() {
        return "我是预言家开奖";
    }

    @Override
    public void job() {
        prophetProcessor.sendMailAward();
    }
}
