package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author suhq
 * @description: 月签到
 * @date 2019-11-07 09:20
 **/
@Service
public class MonthLoginProcessor extends AbstractActivityProcessor {
    @Autowired
    private MonthLoginLogic monthLoginLogic;

    @Autowired
    private PrivilegeService privilegeService;

    public MonthLoginProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.MONTH_LOGIN);
    }

    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        int maxCid =400+DateUtil.getTodayInt() % 100;
        maxCid=Math.min(425,maxCid);
        if (ua == null) {
            ua = UserActivity.fromActivity(gu.getId(), a.gainId(), 1, ca);
            gameUserService.addItem(gu.getId(),ua);
        }
        if (ua.getStatus() == AwardStatus.UNAWARD.getValue()) {
            // 玩家可以补签到 当前天数
            if (ua.getBaseId()<=maxCid) {
                // 需从最早的天数开始补签
                UserActivity preUa = this.activityService.getUserActivity(gu.getId(), ua.getAId(), ua.getBaseId() - 1);
                if (preUa != null && (preUa.getStatus()==AwardStatus.AWARDED.getValue()||preUa.getStatus()==AwardStatus.ENABLE_AWARD.getValue())) {
                    return AwardStatus.ENABLE_REPLENISH;
                }else {
                    return AwardStatus.READY_REPLENISH;
                }
            }
        }
        return AwardStatus.fromValue(ua.getStatus());
    }

    @Override
    public List<Award> getAwardsToShow(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        List<Award> awards = super.getAwardsToShow(gu, ua, ca);
        //签到卡
        if (ca.getNeedValue() == 25) {
            int monthCard = this.getMonthCard();
            awards.add(new Award(monthCard, AwardEnum.KP, 1));
        }
        return awards;
    }

    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        List<Award> awards = super.getAwardsToSend(gu, ua, ca);
        // 特权
        if (ca.getNeedValue() != 25) {
            int doubleTime = this.privilegeService.getMonthAwardDoubleTime(gu);
            awards.stream().forEach(tmp -> tmp.setNum(tmp.getNum() * doubleTime));
        }
        return awards;
    }

    /**
     * 获得签到卡
     *
     * @return
     */
    private int getMonthCard() {
        int month = DateUtil.getMonthSince(this.activityConfig.getMonthCardBaseDate());
        List<Integer> monthCards = this.activityConfig.getMonthCards();
        int cardIndex = (month - 1) % monthCards.size();
        return monthCards.get(cardIndex);

    }

    /**
     * 获取未来2个月和本月的签到卡Id
     * @return
     */
    public List<Integer> getWithinThreeMonthsCards(){
        List<Integer> rd=new ArrayList<>();
        int month = DateUtil.getMonthSince(this.activityConfig.getMonthCardBaseDate());
        List<Integer> monthCards = this.activityConfig.getMonthCards();
        for (int offset=0;offset<3;offset++){
            int cardIndex = (month+ offset - 1 ) % monthCards.size();
            rd.add(monthCards.get(cardIndex));
        }
        return rd;
    }

    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDSuccess success = super.getActivities(uid, activityType);
        RDActivityList rd =(RDActivityList)success;
        monthLoginLogic.buildRd(uid,rd);
        return rd;
    }

    @Override
    public RDCommon replenish(long uid, int sId, CfgActivityEntity ca) {
        RDCommon rd=super.replenish(uid, sId, ca);
        activityService.handleUaProgress(uid, sId, 1, ActivityEnum.MONTH_LOGIN);
        return rd;
    }
}
