package com.bbw.god.activity.holiday.processor.holidaycelebration;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.RedisLockUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * 全服庆典逻辑
 *
 * @author: huanghb
 * @date: 2021/12/21 14:33
 */
@Service
public class GameCelebrationService {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        IActivity activity = this.activityService.getActivity(sid, ActivityEnum.ALL_SERVICE_CELEBRATION);
        return null != activity;
    }

    /**
     * 获得全服庆典不同任务达标进度
     *
     * @param activityId
     * @return
     */
    public int getGameCelbrationTotalProgress(int activityId) {
        return CelebrationPointsEnum.fromValue(activityId).getTargetProgress();
    }

    /**
     * 获得玩家全服庆典全部活动信息（玩家全服庆典为空就初始化）
     *
     * @param uid
     * @param aId
     * @param activityEnum
     * @return
     */
    public List<UserActivity> getUserActivities(long uid, long aId, ActivityEnum activityEnum) {
        //获得玩家全服庆典全部活动信息
        List<UserActivity> userActivities = activityService.getUserActivities(uid, aId, activityEnum);
        if (ListUtil.isNotEmpty(userActivities)) {
            return userActivities;
        }
        //玩家全服庆典为空就初始化
        List<CfgActivityEntity> cfgActivityEntities = ActivityTool.getActivitiesByType(activityEnum);
        cfgActivityEntities.forEach(cfgActivityEntity -> {
            int activityId = cfgActivityEntity.getId();
            int activityStatus = AwardStatus.UNAWARD.getValue();
            String activityName = cfgActivityEntity.getName();
            UserActivity userActivity = UserActivity.instance(uid, aId, 0, activityStatus, activityId, activityName);
            userActivities.add(userActivity);
        });
        gameUserService.addItems(userActivities);
        return userActivities;
    }

    /**
     * 活动进度处理
     *
     * @param uid
     * @param userActivities
     * @return
     */
    public void handleActivityStatus(long uid, List<UserActivity> userActivities) {
        userActivities.forEach(userActivity -> {
            //进度检测,是否可以领取奖励
            boolean isCanReceive = checkCelebrationProgress(uid, userActivity);
            if (!isCanReceive) {
                return;
            }
            userActivity.setStatus(AwardStatus.ENABLE_AWARD.getValue());
        });
    }

    /**
     * 庆典活动进度检测
     *
     * @param uid
     * @param userActivity
     * @return 活动进度是否达到目标进度
     */
    public Boolean checkCelebrationProgress(long uid, UserActivity userActivity) {
        //从枚举中获取全服庆典总积分目标进度
        int gameCelbrationTotalProgress = getGameCelbrationTotalProgress(userActivity.getBaseId());
        //从缓存获取获得全服庆典积分总进度
        int totalProgress = getGameCelbrationTotalProgress();
        //从个人信息获取个人积分进度
        int personalProgress = getPersonalProgress(uid);
        //从活动信息中获取个人目标进度
        int personalTargetProgress = getPersonalTargetProgress(userActivity.getBaseId());

        if (totalProgress < gameCelbrationTotalProgress) {
            return false;
        }
        if (personalProgress < personalTargetProgress) {
            return false;
        }
        if (userActivity.getStatus() == AwardStatus.AWARDED.getValue()) {
            return false;
        }
        return true;
    }

    /**
     * 筛选出活动目标进度
     *
     * @param activityId
     * @return
     */
    private int getPersonalTargetProgress(int activityId) {
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivity(activityId);
        return cfgActivityEntity.getNeedValue();
    }

    /**
     * 对玩家全服庆典活动信息进行封装
     *
     * @param userActivities
     * @return
     */
    public List<RDGameCelebration.RDActivity> getRdActivityInfos(List<UserActivity> userActivities) {
        List<RDGameCelebration.RDActivity> rdActivities = new ArrayList<>();
        userActivities.forEach(userActivity ->
                {
                    CfgActivityEntity cfgActivityEntity = ActivityTool.getActivity(userActivity.getBaseId());
                    RDGameCelebration.RDActivity rdActivity = new RDGameCelebration.RDActivity();
                    rdActivity.setId(userActivity.getBaseId());
                    rdActivity.setStatus(userActivity.getStatus());
                    Award award = getActivityAwards(userActivity.getBaseId()).get(0);
                    rdActivity.setAward(award);
                    rdActivity.setTitle(cfgActivityEntity.getName());
                    rdActivities.add(rdActivity);
                }
        );
        return rdActivities;
    }

    /**
     * 获得活动奖励
     *
     * @param
     * @return
     */
    public List<Award> getActivityAwards(int activityId) {
        CfgActivityEntity cfgActivityEntity = ActivityTool.getActivity(activityId);
        return awardService.parseAwardJson(cfgActivityEntity.getAwards(), Award.class);
    }

    /**
     * 获得全服庆典积分总进度
     *
     * @return
     */
    public int getGameCelbrationTotalProgress() {
        String cacheKey = getGameCelebrationPointKey();
        //读取全服庆典积分总进度信息
        Integer gameCelbrationTotalProgress = GameDataTimeLimitCacheUtil
                .getFromCache(cacheKey, Integer.class);
        if (null != gameCelbrationTotalProgress) {
            return gameCelbrationTotalProgress;
        }
        //读取不到则初始化为0
        return 0;
    }


    /**
     * 获得全服庆典个人积分
     *
     * @param uid
     * @return
     */
    public int getPersonalProgress(long uid) {
        return userTreasureService.getTreasureNum(uid, TreasureEnum.CELEBRATION_POINTS.getValue());
    }

    /**
     * 对任务根据完成进度进行排序
     *
     * @param rdActivities
     * @return
     */
    public List<RDGameCelebration.RDActivity> sortRDActivityInfos(List<RDGameCelebration.RDActivity> rdActivities) {
        List<RDGameCelebration.RDActivity> rdActivityInfoList = new ArrayList<>();
        //筛选未领取的任务
        List<RDGameCelebration.RDActivity> completedEvent = rdActivities.stream()
                .filter(rdActivityInfo -> rdActivityInfo.getStatus() != AwardStatus.AWARDED.getValue())
                .collect(Collectors.toList());
        //筛选出已领取的任务
        List<RDGameCelebration.RDActivity> unfinishedEvent = rdActivities.stream()
                .filter(rdActivityInfo -> rdActivityInfo.getStatus() == AwardStatus.AWARDED.getValue())
                .collect(Collectors.toList());
        rdActivityInfoList.addAll(completedEvent);
        rdActivityInfoList.addAll(unfinishedEvent);
        return rdActivityInfoList;
    }

    /**
     * 全服庆典积分增加(加锁，线程安全)
     * 全服庆典增加任务进度
     * (个人积分即为玩家道具庆典积分的数量)
     *
     * @param evTreasure
     */
    public void addCelebrationPointProgress(EVTreasure evTreasure) {
        Integer celebrationPoint = evTreasure.getNum();
        String lockKey = getGameCelebrationPointLockKey();
        redisLockUtil.doSafe(lockKey, 5, tmp -> {
            //增加全服庆典总进度
            int gameCelbrationTotalProgress = getGameCelbrationTotalProgress();
            gameCelbrationTotalProgress += celebrationPoint;
            updataGameCelbrationTotalProgress(gameCelbrationTotalProgress);
            return true;
        });
    }

    /**
     * 更新全服积分总进度缓存，该数据存放在缓存里
     *
     * @param totalProgress
     */
    public void updataGameCelbrationTotalProgress(int totalProgress) {
        String cachekey = getGameCelebrationPointKey();
        GameDataTimeLimitCacheUtil.cache(cachekey,
                totalProgress, DateUtil.SECOND_ONE_DAY * 11);
    }


    /**
     * 获取全服庆典积分的锁的key
     *
     * @return
     */
    private String getGameCelebrationPointLockKey() {
        return "game" + SPLIT + "celebration" + SPLIT + "point" + SPLIT + "lock" + SPLIT + "init";
    }

    /**
     * 全服庆典积分的key
     *
     * @return
     */
    private String getGameCelebrationPointKey() {
        return "game" + SPLIT + "celebration" + SPLIT + "point";
    }


}
