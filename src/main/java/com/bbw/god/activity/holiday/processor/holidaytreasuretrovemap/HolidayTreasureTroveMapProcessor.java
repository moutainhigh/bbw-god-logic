package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 节日活动-寻藏宝图
 *
 * @author: huanghb
 * @date: 2022/2/8 13:42
 */
@Service
public class HolidayTreasureTroveMapProcessor extends AbstractActivityProcessor {
    @Autowired
    private TreasureTroveMapService treasureTroveMapService;

    public HolidayTreasureTroveMapProcessor() {

        this.activityTypeList = Arrays.asList(ActivityEnum.FIND_TREASURE_MAP);
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
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.FIND_TREASURE_MAP.getValue());
        IActivity activity = activityService.getActivity(sid, activityEnum);
        return activity != null;
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
    public long getRemainTime(long uid, int sid, IActivity activity) {
        if (activity.gainEnd() != null) {
            return activity.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 获取活动信息
     *
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = treasureTroveMapService.getUserTreasureTroveMap(uid);
        RDTreasureTroveMap rd = RDTreasureTroveMap.instance(userTreasureTroveMap);
        return rd;
    }

    /**
     * 刷新翻牌挑战
     *
     * @param uid
     * @return
     */
    public RDTreasureTroveMap refreshFlopChallenge(long uid, Integer treasureTroveMapLevel) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.refreshFlopChallenge(uid, treasureTroveMapLevel);
    }

    /**
     * 翻牌
     *
     * @param uid
     * @return
     */
    public RDCommon flop(long uid, Integer flopIndex) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.flop(uid, flopIndex);
    }

    /**
     * 领取连线奖励
     *
     * @param uid
     * @return
     */
    public RDCommon receiveConnectionAwards(long uid, Integer connectionAwardId) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.receiveConnectionAwards(uid, connectionAwardId);
    }

    /**
     * 领取翻牌目标奖励
     *
     * @param uid
     * @return
     */
    public RDCommon receiveFlopTargetAwards(long uid, Integer targetId) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.receiveFlopTargetAwards(uid, targetId);
    }

    /**
     * 获得藏宝图奖励用于展示
     *
     * @param uid
     * @return
     */
    public RDTreasureTroveMapAward getTreasureTroveMapAwardToShow(long uid, Integer treasureTroveMapLevel) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.getTreasureTroveMapToShow(uid, treasureTroveMapLevel);
    }

    /**
     * 领取藏宝图奖励奖励
     *
     * @param uid
     * @return
     */
    public RDCommon receiveTreasureTroveMapAwards(long uid, int treasureTroveMapLevel) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        return treasureTroveMapService.receiveTreasureTroveMapAwards(uid, treasureTroveMapLevel);
    }
}

