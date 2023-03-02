package com.bbw.god.activity.config;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.random.config.RandomKeys;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Configuration
@Component
public class ActivityConfig {
    // 榜单显示的人数
    private final Integer numRankersToShow = 10;
    // 冲榜发放奖励的人数限制
    private final Integer numOfRankAwardLimit = 200;
    // 补充体力所需要的元宝
    private final Integer needGoldToReplenishDice = 30;
    // 补领签到所需要的元宝
    private final Integer needGoldToReplenishMonthLogin = 50;
    // 补领充值签到卡所需要的元宝
    private final Integer needGoldToReplenishRechargeSign = 30;
    // 签到卡时间基准
    private final Date monthCardBaseDate = DateUtil.fromDateTimeString("2019-04-01 00:00:00");
    // 129日游神 133金光仙
    // 229余化 230蚕丛 231灵宝法师 232白泽 235毗卢仙
    // 331无当圣母 337螭龙 338乌云仙 339长耳定光仙 340余元
    // 426巡夜女使 431夜游神 433五夷散人
    // 532龟灵圣母 533山魈王 534虬首仙
    private final List<Integer> monthCards = Arrays.asList(232, 339, 129, 229, 331, 426, 532, 230, 337, 431, 533, 231, 338, 133, 235, 340, 433, 534);
    // 充值前七周加奖
    private final List<Integer> rechargeCard = Arrays.asList(0, 325, 127, 128);
    // 充值第7周开始加奖
    private final Award rechargeCardAward = new Award(AwardEnum.KP, 4, 1, RandomKeys.RECHARGE_RANK_ADDITION_CARD, 1);
    // 轮次活动
    private List<ActivityEnum> roundActivities = Arrays.asList(ActivityEnum.ACC_R);

}
