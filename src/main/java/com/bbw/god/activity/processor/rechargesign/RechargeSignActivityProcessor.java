package com.bbw.god.activity.processor.rechargesign;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 充值签到
 *
 * @author: suhq
 * @date: 2021/8/3 9:08 上午
 */
@Service
public class RechargeSignActivityProcessor extends AbstractActivityProcessor {
    @Autowired
    private UserCardService userCardService;

    public RechargeSignActivityProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.RECHARGE_SIGN);
    }

    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        //未激活
        if (ua == null) {
            return AwardStatus.UNAWARD;
        }
        //未到最后一天，直接返回状态
        if (DateUtil.toDateInt(a.gainEnd()) != DateUtil.getTodayInt()) {
            return AwardStatus.fromValue(ua.getStatus());
        }
        //最后一天，不是不能领取直接返回
        if (ua.getStatus() != AwardStatus.UNAWARD.getValue()) {
            return AwardStatus.fromValue(ua.getStatus());
        }

        // 最后一天，不能领取的，进行补签状态处理
        // 需从最早的天数开始补签
        UserActivity preUa = this.activityService.getUserActivity(gu.getId(), ua.getAId(), ua.getBaseId() - 1);
        if (preUa != null && (preUa.getStatus() == AwardStatus.AWARDED.getValue() || preUa.getStatus() == AwardStatus.ENABLE_AWARD.getValue())) {
            return AwardStatus.ENABLE_REPLENISH;
        } else {
            return AwardStatus.READY_REPLENISH;
        }

    }

    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        List<Award> awards = super.getAwardsToSend(gu, ua, ca);
        // 第7天
        if (ca.getNeedValue() == 7) {
            List<Award> cardAwards = awards.stream().filter(tmp -> tmp.getItem() == AwardEnum.KP.getValue()).collect(Collectors.toList());
            List<Integer> cardIds = cardAwards.stream().map(Award::getAwardId).collect(Collectors.toList());
            List<UserCard> userCards = userCardService.getUserCards(gu.getId(), cardIds);
            if (ListUtil.isNotEmpty(userCards)) {
                //持有的卡牌ID
                List<Integer> ownCardIds = userCards.stream().map(UserCard::getBaseId).collect(Collectors.toList());
                //炼技保护符奖励，用于概率叠加
                Award lianJBHFAward = awards.stream()
                        .filter(tmp -> tmp.getItem() == AwardEnum.FB.getValue() && tmp.getAwardId() == TreasureEnum.LJBHF.getValue())
                        .findFirst().orElse(null);
                //炼技保护符概率叠加
                Iterator<Award> iterator = awards.iterator();
                while (iterator.hasNext()) {
                    Award award = iterator.next();
                    if (award.getItem() == AwardEnum.KP.getValue() && ownCardIds.contains(award.gainAwardId())) {
                        lianJBHFAward.setProbability(lianJBHFAward.getProbability() + award.getProbability());
                        iterator.remove();
                    }
                }
            }
            // 概率集合
            List<Integer> props = awards.stream().map(Award::getProbability).collect(Collectors.toList());
            // 随机索引
            int index = PowerRandom.getIndexByProbs(props, 100);
            //设置返回的待发放的奖励
            Award finalAward = awards.get(index);
            awards.clear();
            awards.add(finalAward);
        }

        return awards;
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDSuccess success = super.getActivities(uid, activityType);
        RDActivityList rd = (RDActivityList) success;
        if (rd.getTotalProgress() == 0) {
            rd.setRechargeId(CfgProductGroup.CfgProduct.RECHARGE_SIGN);
        }
        UserRechargeSignRecord rechargeSignRecord = gameUserService.getSingleItem(uid, UserRechargeSignRecord.class);
        if (null != rechargeSignRecord && ListUtil.isNotEmpty(rechargeSignRecord.getAwarded())) {
            List<RDAward> rdAwards = RDAward.getInstances(rechargeSignRecord.getAwarded());
            rd.setAwardeds(rdAwards);
        }
        return rd;
    }

    @Override
    public RDCommon replenish(long uid, int sId, CfgActivityEntity ca) {
        RDCommon rd = super.replenish(uid, sId, ca);
        activityService.handleUaProgress(uid, sId, 1, ActivityEnum.MONTH_LOGIN);
        return rd;
    }

    /**
     * 活动剩余时间
     *
     * @param uid
     * @param sid
     * @param a
     * @return
     */
    @Override
    public long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
