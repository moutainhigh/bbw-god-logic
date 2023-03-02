package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 合服登录
 *
 * @author: huanghb
 * @date: 2021/12/30 13:47
 */
@Service
public class HolidayCombinedServiceLoginProcessor extends AbstractActivityProcessor {
    @Autowired
    private PrivilegeService privilegeService;
    /** 登录普通奖励 */
    private static final int LOGIN_COMMON_AWARD = 1;
    /** 登录战令奖励 */
    private static final int LOGIN_WAR_TOKEN_AWARD = 2;
    /** 登录天灵印奖励 */
    private static final int LOGIN_TIANLING_AWARD = 3;

    public HolidayCombinedServiceLoginProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.COMBINED_SERVICE_LOGIN);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.COMBINED_SERVICE_LOGIN.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return null != a;
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
        //获取活动实例
        int sid = gameUserService.getActiveSid(uid);
        IActivity activity = this.activityService.getActivity(sid, ActivityEnum.fromValue(activityType));
        //获得单个玩家节日每日累充所有活动集合
        List<UserActivity> userActivities = activityService.getUserActivities(uid, activity.gainId(), ActivityEnum.COMBINED_SERVICE_LOGIN);
        //获得需要添加可选奖励的活动
        List<RDActivityItem> rdActivityItems = rd.getItems();
        List<CfgActivityEntity> cfgActivityEntities = ActivityTool.getActivitiesByType(ActivityEnum.COMBINED_SERVICE_LOGIN);

        for (CfgActivityEntity cfgActivityEntity : cfgActivityEntities) {
            RDActivityItem rdActivityItem = rdActivityItems.stream().filter(tmp -> tmp.getId().equals(cfgActivityEntity.getId())).findFirst().orElse(null);
            //活动不存在
            if (null == rdActivityItem) {
                throw new ExceptionForClientTip("activity.not.exist");
            }
            //添加活动序列号
            rdActivityItem.setSeries(cfgActivityEntity.getSeries());
            //是否可以领取活动
            if (isCanReceiveWarTokenOrTianLingAwards(uid, userActivities, cfgActivityEntity)) {
                continue;
            }
            //更新状态为不可领取
            rdActivityItem.setStatus(AwardStatus.UNAWARD.getValue());
        }
        return rd;
    }

    /**
     * 是否可以领取战令或者天灵印奖励
     *
     * @param uid
     * @param userActivities
     * @param cfgActivityEntity
     * @return
     */
    private boolean isCanReceiveWarTokenOrTianLingAwards(long uid, List<UserActivity> userActivities, CfgActivityEntity cfgActivityEntity) {
        //是否拥有天灵印
        boolean isOwnTianLing = privilegeService.isOwnTianLing(uid);
        //获得用户活动信息
        UserActivity userActivity = userActivities.stream().filter(tmp -> tmp.getBaseId().equals(cfgActivityEntity.getId())).findFirst().orElse(null);
        //活动信息为空
        if (null == userActivity) {
            return true;
        }
        //是否登录天数不足
        boolean isInsufficientLoginDays = userActivity.getStatus() != AwardStatus.ENABLE_AWARD.getValue();
        if (isInsufficientLoginDays) {
            return true;
        }
        //是否是普通登录奖励
        boolean isComonLoginAward = cfgActivityEntity.getSeries() == LOGIN_COMMON_AWARD;
        if (isComonLoginAward) {
            return true;
        }
        //用户战令
        UserWarToken userWarToken = gameUserService.getSingleItem(uid, UserWarToken.class);
        //是否可以领取登录战令奖励
        boolean isCanReceiveWarTokenAwards = cfgActivityEntity.getSeries() == LOGIN_WAR_TOKEN_AWARD && userWarToken != null && userWarToken.getSupToken() != 0;
        if (isCanReceiveWarTokenAwards) {
            return true;
        }
        //是否可以领取登录天灵印奖励
        boolean isCanReceiveTianLingAwards = cfgActivityEntity.getSeries() == LOGIN_TIANLING_AWARD && isOwnTianLing;
        if (isCanReceiveTianLingAwards) {
            return true;
        }
        return false;
    }

    /**
     * 获得可领取奖励数量
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        //获取活动信息
        ActivityEnum activityEnum = ActivityEnum.fromValue(a.gainType());
        List<UserActivity> uas = this.activityService.getUserActivities(gu.getId(), a.gainId(), activityEnum);
        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        int ableAwardNum = 0;
        //是否天灵印
        boolean isOwnTianLing = privilegeService.isOwnTianLing(gu.getId());
        for (CfgActivityEntity ca : cas) {
            UserActivity ua = uas.stream().filter(uaTmp -> uaTmp.getBaseId().intValue() == ca.getId()).findFirst().orElse(null);
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            //奖励不可领取
            if (status != AwardStatus.ENABLE_AWARD) {
                continue;
            }
            //是否是普通登录奖励
            boolean isComonLoginAward = ca.getSeries() == LOGIN_COMMON_AWARD;
            if (isComonLoginAward) {
                ableAwardNum++;
                continue;
            }
            //是否可以领取登录战令奖励
            UserWarToken userWarToken = gameUserService.getSingleItem(gu.getId(), UserWarToken.class);
            boolean isCanReceiveWarTokenAwards = ca.getSeries() == LOGIN_WAR_TOKEN_AWARD && userWarToken != null && userWarToken.getSupToken() != 0;
            if (isCanReceiveWarTokenAwards) {
                ableAwardNum++;
                continue;
            }
            //是否可以领取登录天灵印奖励
            boolean isCanReceiveTianLingAwards = ca.getSeries() == LOGIN_TIANLING_AWARD && isOwnTianLing;
            if (isCanReceiveTianLingAwards) {
                ableAwardNum++;
                continue;
            }
        }
        return ableAwardNum;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }


}
