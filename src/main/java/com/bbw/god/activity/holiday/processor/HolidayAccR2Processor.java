package com.bbw.god.activity.holiday.processor;

import com.bbw.common.CloneUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日累充(UI)
 * @date 2020-01-13 09:20
 **/
@Service
public class HolidayAccR2Processor extends AbstractActivityProcessor {
    private static final List<Integer> ABLE_CHOOSE_CARD_IDS = Arrays.asList(147, 243, 354, 443, 541);

    public HolidayAccR2Processor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_ACC_R_2);
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivityByType(ActivityEnum.fromValue(activityType));
        RDActivityItem rdActivity = rd.getItems().stream().filter(tmp -> tmp.getId().equals(cfgActivityEntity.getId())).findFirst().orElse(null);
        List<Award> awards = ABLE_CHOOSE_CARD_IDS.stream().map(tmp -> Award.instance(tmp, AwardEnum.KP, 1)).collect(Collectors.toList());
        rdActivity.setAbleChooseAwards(awards);
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.fromValue(activityType));
        UserActivity ua = activityService.getUserActivity(uid, a.gainId(), cfgActivityEntity.getId());
        int extraAwardStatus = 0;
        if (null != ua && null != ua.getAwardIndex() && ua.getAwardIndex() >= 0 && ua.getAwardIndex() <= awards.size()) {
            Award award = awards.get(ua.getAwardIndex());
            Award clone = CloneUtil.clone(award);
            rdActivity.addAward(clone);
            extraAwardStatus = 1;
        }
        rdActivity.setExtraAwardStatus(extraAwardStatus);
        return rd;
    }

    /**
     * 获得活动奖励
     *
     * @param gu
     * @param ua
     * @param ca
     * @return
     */
    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivityByType(ActivityEnum.HOLIDAY_ACC_R_2);
        if (null != cfgActivityEntity && ca.getId().equals(cfgActivityEntity.getId())) {
            Integer awardIndex = ua.getAwardIndex();
            if (null == awardIndex || -1 == awardIndex) {
                throw new ExceptionForClientTip("award.not.valid.choose");
            }
            Integer cardId = ABLE_CHOOSE_CARD_IDS.get(awardIndex);
            return Arrays.asList(Award.instance(cardId, AwardEnum.KP, 1));
        }
        return getAwardsToShow(gu, ua, ca);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }
}
