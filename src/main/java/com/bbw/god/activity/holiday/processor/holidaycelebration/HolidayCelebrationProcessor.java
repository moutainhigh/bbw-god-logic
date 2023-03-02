package com.bbw.god.activity.holiday.processor.holidaycelebration;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 全服庆典
 *
 * @author: huanghb
 * @date: 2021/12/15 16:16
 */
@Service
public class HolidayCelebrationProcessor extends AbstractActivityProcessor {
    @Autowired
    private GameCelebrationService celebrationService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private AwardService awardService;


    public HolidayCelebrationProcessor() {

        this.activityTypeList = Arrays.asList(ActivityEnum.ALL_SERVICE_CELEBRATION);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 活动剩余时间
     *
     * @param uid
     * @param sid
     * @param activity
     * @return
     */
    @Override
    protected long getRemainTime(long uid, int sid, IActivity activity) {
        if (activity.gainEnd() != null) {
            return activity.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }


    /**
     * 领取全服庆典成就进度奖励
     *
     * @param uid
     * @param sid
     * @param activityId
     * @return
     */
    public RDCommon receiveCelebrationAward(long uid, int sid, int activityId) {
        CelebrationPointsEnum celebrationPointsEnum = CelebrationPointsEnum.fromValue(activityId);
        if (null == celebrationPointsEnum) {
            throw new ExceptionForClientTip("client.request.unvalid.arg");
        }

        IActivity activity = activityService.getActivity(sid, ActivityEnum.ALL_SERVICE_CELEBRATION);
        if (null == activity) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //获取玩家单个活动信息
        UserActivity userActivity = activityService.getUserActivity(uid, activity.gainId(), activityId);
        //是否可以领取奖励
        if (AwardStatus.AWARDED.getValue() == userActivity.getStatus()) {
            throw new ExceptionForClientTip("activity.awarded");
        }
        if (AwardStatus.ENABLE_AWARD.getValue() != userActivity.getStatus()) {
            throw new ExceptionForClientTip("rechargeActivity.cant.award");
        }
        //重置为已经领取
        userActivity.setStatus(AwardStatus.AWARDED.getValue());
        gameUserService.updateItem(userActivity);
        RDCommon rd = new RDCommon();
        //发放奖励
        List<Award> awards = celebrationService.getActivityAwards(userActivity.getBaseId());
        awardService.fetchAward(uid, awards, WayEnum.ACTIVITY, "", rd);
        return rd;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity activity = activityService.getActivity(sid, activityEnum);
        if (null == activity) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //构造玩家庆典活动信息
        List<UserActivity> userActivities = celebrationService.getUserActivities(uid, activity.gainId(), activityEnum);
        celebrationService.handleActivityStatus(uid, userActivities);
        List<RDGameCelebration.RDActivity> rdActivities = celebrationService.getRdActivityInfos(userActivities);
        rdActivities = celebrationService.sortRDActivityInfos(rdActivities);

        //庆典进度
        int personalProgress = celebrationService.getPersonalProgress(uid);
        int gameCelbrationTotalProgress = celebrationService.getGameCelbrationTotalProgress();

        //处理剩余时间
        long remainTime = getRemainTime(uid, sid, activity);
        gameUserService.updateItems(userActivities);
        
        RDGameCelebration rd = new RDGameCelebration();
        rd.setTotalProgress(gameCelbrationTotalProgress);
        rd.setRDActivitys(rdActivities);
        rd.setPersonalProgress(personalProgress);
        rd.setRemainTime(remainTime);
        rd.setCurType(activityType);
        return rd;
    }


}
