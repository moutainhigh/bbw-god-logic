package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.god.statistics.CardSkillStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 卡牌技能推荐更新定时任务
 *
 * @author fzj
 * @date 2022/4/5 10:52
 */
@Component("cardSkillRecommendUpdateJob")
@Slf4j
public class CardSkillRecommendUpdateJob extends GameJob {
    @Autowired
    CardSkillStatisticService cardSkillStatisticService;

    @Override
    public String getJobDesc() {
        return "卡牌技能推荐更新";
    }

    @Override
    public void job() {
        //获得半年前的时间
        Date halfYeaAgo = DateUtil.addMonths(DateUtil.now(), -6);
        int sinceDateInt = DateUtil.toDateInt(halfYeaAgo);

        cardSkillStatisticService.doStatistic(sinceDateInt);
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
