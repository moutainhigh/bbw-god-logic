package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 新手礼包处理器  上仙礼包
 * @date 2020/7/6 10:30
 **/
@Service
public class NewerPackageProcessor extends AbstractActivityProcessor {

    @Autowired
    private MallService mallService;

    public NewerPackageProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.NEWER_PACKAGE);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        GameUser gu = gameUserService.getGameUser(uid);
        Date regTime = gu.getRoleInfo().getRegTime();
        Date endDate = DateUtil.addSeconds(regTime, 7 * 24 * 60 * 60 - 1);
        return endDate.getTime() - DateUtil.now().getTime();
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        return 0;
    }

    @Override
    public boolean isAwardedAllAwards(long uid, List<UserActivity> uas, ActivityEnum activityEnum) {
        UserMallRecord userMallRecord = mallService.getUserMallRecord(uid, 1220);
        UserMallRecord userMallRecord2 = mallService.getUserMallRecord(uid, 1221);
        if ((userMallRecord2 != null && userMallRecord2.getNum() > 0) || (userMallRecord != null && userMallRecord.getNum() > 0)) {
            return true;
        }
        IActivity a = this.activityService.getActivity(gameUserService.getActiveSid(uid), activityEnum);
        if (a == null) {
            return true;
        }
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        GameUser gu = gameUserService.getGameUser(uid);
        for (CfgActivityEntity ca : cas) {
            int caId = ca.getId();
            UserActivity ua = uas.stream().filter(tmp -> tmp.getBaseId() == caId).findFirst().orElse(null);
            // 获取活动的状态
            if (ua == null) {
                return false;
            }
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            if (AwardStatus.AWARDED.equals(status)) {
                return false;
            }
        }
        return true;
    }
}
