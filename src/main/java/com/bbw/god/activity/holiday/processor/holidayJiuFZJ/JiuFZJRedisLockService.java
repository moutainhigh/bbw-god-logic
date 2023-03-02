package com.bbw.god.activity.holiday.processor.holidayJiuFZJ;

import com.bbw.common.ListUtil;
import com.bbw.common.lock.redis.annotation.RedisLock;
import com.bbw.common.lock.redis.annotation.RedisLockParam;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 酒逢知己redis锁
 *
 * @author: huanghb
 * @date: 2023/2/16 9:35
 */
@Slf4j
@Component
public class JiuFZJRedisLockService {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 添加多动进度
     *
     * @param recipientId        收件人id
     * @param carefreeBrewingNum 逍遥酿数量
     * @param activityEnum       活动枚举
     * @param a                  活动实例
     * @param cas                活动配置
     */
    @RedisLock(key = "game:var:JiuFZJLock")
    public void addActivityProgress(@RedisLockParam long recipientId, int carefreeBrewingNum, ActivityEnum activityEnum, IActivity a, List<CfgActivityEntity> cas) {
        List<UserActivity> userActivitiesToAdd = new ArrayList<>();
        List<UserActivity> userActivitiesToUpdate = new ArrayList<>();

        //获取玩家活动信息
        List<UserActivity> uas = this.activityService.getUserActivities(recipientId, a.gainId(), activityEnum);
        for (CfgActivityEntity ca : cas) {
            int caId = ca.getId();
            UserActivity userActivity = uas.stream().filter(ua -> ua.getBaseId() == caId).findFirst().orElse(null);
            //不存在活动信息则直接生成
            if (null == userActivity) {
                userActivity = UserActivity.fromActivity(recipientId, a.gainId(), carefreeBrewingNum, ActivityTool.getActivity(caId));
                userActivitiesToAdd.add(userActivity);
                continue;
            }
            //不是可完成任务直接返回
            if (AwardStatus.UNAWARD.getValue() != userActivity.getStatus()) {
                continue;
            }
            //添加活动进度
            userActivity.addProgress(carefreeBrewingNum, ca);
            userActivitiesToUpdate.add(userActivity);
        }

        if (ListUtil.isNotEmpty(userActivitiesToAdd)) {
            gameUserService.addItems(userActivitiesToAdd);
        }
        if (ListUtil.isNotEmpty(userActivitiesToUpdate)) {
            gameUserService.updateItems(userActivitiesToUpdate);
        }
    }
}
