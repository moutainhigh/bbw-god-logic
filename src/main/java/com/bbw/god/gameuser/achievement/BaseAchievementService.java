package com.bbw.god.gameuser.achievement;

import com.bbw.common.lock.SyncLockUtil;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 基础成就service
 * @date 2020/5/14 10:35
 **/
@Service
public abstract class BaseAchievementService {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    private UserAchievementLogic userAchievementLogic;
    @Autowired
    protected StatisticServiceFactory statisticServiceFactory;
    @Autowired
    private SyncLockUtil syncLockUtil;

    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    public abstract int getMyAchievementId();

    /**
     * 获取当前成就所需值
     *
     * @return 当前成就所需值
     */
    public int getMyNeedValue() {
        CfgAchievementEntity achievement = AchievementTool.getAchievement(getMyAchievementId());
        return achievement.getValue();
    }

    /**
     * 获取当前成就进度(用于展示给客户端)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    public abstract int getMyProgress(long uid, UserAchievementInfo info);

    /**
     * 获取当前成就进度(用于判断成就是否完成)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    public int getMyValueForAchieve(long uid, UserAchievementInfo info) {
        return getMyProgress(uid, info);
    }

    /**
     * 判断当前成就是否完成（已领取的也算）
     *
     * @param info 成就信息
     * @return 当前成就是否完成（已领取的也算）
     */
    public boolean isAccomplished(UserAchievementInfo info) {
        return isAccomplished(info, getMyAchievementId());
    }

    public boolean isAccomplished(UserAchievementInfo info, int achievementId) {
        if (null == info) {
            return false;
        }
        return info.getAccomplishedIds().get(achievementId) || info.getAwardedIds().get(achievementId);
    }

    /**
     * 达成成就
     *
     * @param uid        玩家id
     * @param totalValue 玩家目前成就总进度
     */
    public void achieve(long uid, long totalValue, UserAchievementInfo info, RDCommon rd) {
        if (isAccomplished(info)) {
            return;
        }
        if (totalValue >= getMyNeedValue()) {
            if (null == info) {
                info = (UserAchievementInfo) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                    UserAchievementInfo userAchievementInfo = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
                    if (null == userAchievementInfo) {
                        userAchievementInfo = userAchievementLogic.initUserAchievementInfo(uid);
                        gameUserService.addItem(uid, userAchievementInfo);
                    }
                    return userAchievementInfo;
                });
            }
            int achievementId = getMyAchievementId();
            info.accomplishAchievement(achievementId);
            gameUserService.updateItem(info);
            if (null != rd) {
                rd.addAchievement(getMyAchievementId());
            }
            CfgAchievementEntity cfgAchievement = AchievementTool.getAchievement(achievementId);
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.ACHIEVEMENT, cfgAchievement.getType(),
                    achievementId);
        }
    }
}
