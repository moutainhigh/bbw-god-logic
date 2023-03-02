package com.bbw.god.activity.worldcup.job;

import com.bbw.god.activity.worldcup.processor.QuizKingProcessor;
import com.bbw.god.job.game.GameJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 我是竞猜王定时奖励发送
 * @author: hzf
 * @create: 2022-11-21 16:45
 **/
@Component("userQuizKingAwardsJob")
@Slf4j
public class UserQuizKingAwardsJob extends GameJob {
    @Autowired
    private QuizKingProcessor quizKingProcessor;

    @Override
    public String getJobDesc() {
        return "我是竞猜王开奖";
    }

    @Override
    public void job() {
        quizKingProcessor.sendMailAward();
    }
}
