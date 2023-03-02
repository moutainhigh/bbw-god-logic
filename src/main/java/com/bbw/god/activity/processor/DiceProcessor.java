package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 月签到
 * @date 2019-11-07 09:20
 **/
@Service
public class DiceProcessor extends AbstractActivityProcessor {
    private static final Integer before12 = 1;
    private static final Integer after12 = 2;

    public DiceProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.DICE);
    }

    @Override
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca) {
        if (ua != null) {
            return AwardStatus.fromValue(ua.getStatus());
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer series = ca.getSeries();
        if ((before12.equals(series) && hour < ca.getNeedValue()) || (after12.equals(series) && hour >= ca.getNeedValue())) {
            if (ua != null) {
                return AwardStatus.AWARDED;
            }
            return AwardStatus.ENABLE_AWARD;
        } else if (before12.equals(series) && hour >= ca.getNeedValue()) {
            return AwardStatus.ENABLE_REPLENISH;
        } else {
            return AwardStatus.UNAWARD;
        }
    }

    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        List<Award> awards = super.getAwardsToSend(gu, ua, ca);
        Date regTime = gu.getRoleInfo().getRegTime();
        int daysBetween = DateUtil.getDaysBetween(regTime, DateUtil.now());
        // 第二天和第三天的时候，每次可以领200点体力
        if (daysBetween > 0 && daysBetween < 3) {
            awards.forEach(tmp -> tmp.setNum(200));
        }
        if (this.activityService.isActive(gu.getServerId(), ActivityEnum.DOUBLE_DICE)) {
            awards = awards.stream()
                    .map(award -> new Award(AwardEnum.fromValue(award.getItem()), award.getNum() * 2))
                    .collect(Collectors.toList());
        }
        return awards;
    }

    @Override
    protected void updateAwardedStatus(GameUser gu, long aId, int awardIndex, CfgActivityEntity ca) {
        long uid = gu.getId();
        UserActivity ua = UserActivity.fromActivityAsDice(uid, aId, ca);
        activityService.addUserActivity(uid,ua);
    }
}
