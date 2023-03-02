package com.bbw.god.game.zxz.service;

import com.bbw.common.DateUtil;
import com.bbw.god.game.zxz.cfg.CfgAutoRefreshRule;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 诛仙阵刷新服务
 *
 * @author: suhq
 * @date: 2022/9/30 9:38 上午
 */
@Service
public class ZxzRefreshService {
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 手动根据难度刷新攻打记录
     * @param uid
     * @param difficulty
     */
    public void manualRefreshDifficulty(long uid, Integer difficulty){
        //获取玩家的区域的攻打记录
        List<UserZxzRegionInfo> userZxzRegionInfos = zxzService.getUserZxzRegions(uid,difficulty);
        for (UserZxzRegionInfo regionInfo : userZxzRegionInfos) {
            manualRefreshDifficultyRegion(uid,difficulty,regionInfo.getRegionId());
        }

    }
    /**
     * 手动根据区域刷新攻打记录
     * @param uid
     * @param difficulty
     */
    public void manualRefreshDifficultyRegion(long uid, Integer difficulty, Integer regionId){
        //获取玩家的区域的攻打记录
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(uid, regionId);
        userZxzRegion.setInto(false);
        List<UserZxzRegionDefender> regionDefenders = userZxzRegion.getRegionDefenders();
        //初始化关卡
        userZxzRegion.refreshRegion(regionDefenders);
        gameUserService.updateItem(userZxzRegion);

        UserZxzInfo userZxz = zxzService.getUserZxz(uid);
        UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(difficulty);
        if (zxzService.ifRegionAllClearance(uid,difficulty)) {
            userZxzDifficulty.setStatus(ZxzStatusEnum.PASSED.getStatus());
        }else {
            userZxzDifficulty.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        }
        gameUserService.updateItem(userZxz);

    }


    /**
     * 计算难度的刷新时间
     *
     * @param difficulty
     * @return 返回距离下一次刷新剩余的秒数
     */
    public static boolean isToRefresh(Integer difficulty, Date lastRefreshDate) {
        Date now = DateUtil.now();
        boolean ifRefresh = false;
        List<CfgAutoRefreshRule.CfgRefreshTime> refreshRules = ZxzTool.getRefreshTime(difficulty);
        for (CfgAutoRefreshRule.CfgRefreshTime refreshRule : refreshRules) {

            List<Integer> weekDays = refreshRule.getWeekDays();
            for (int i = 0; i < weekDays.size(); i++) {
                int weekDay = weekDays.get(i);
                //判断是不是每天刷新
                boolean isRefrshPerDay = weekDay == 0;
                if (isRefrshPerDay) {
                    return DateUtil.getDaysBetween(lastRefreshDate, now) > 0;
                }
                //判断是否要刷新
                ifRefresh = ifRefresh(lastRefreshDate, weekDay, refreshRule.getHour());
            }
        }
        return ifRefresh;
    }

    /**
     * 单独将周一零点刷新提取出来
     * @param lastRefreshDate
     * @return
     */
    public static boolean isToRefresh(Date lastRefreshDate) {
        int refreshHour = 0;
        int weekDay = 1;
        return ifRefresh(lastRefreshDate,weekDay,refreshHour);
    }

    /**
     * 计算四圣挑战刷新时间
     * @param lastRefreshDate
     * @return
     */
    public static boolean fourSaintsIsToRefresh(Date lastRefreshDate) {
        CfgFourSaintsEntity.CfgAutoRefreshRule autoRefreshRule = CfgFourSaintsTool.getAutoRefreshRule();
        return ifRefresh(lastRefreshDate,autoRefreshRule.getWeekDay(),autoRefreshRule.getHour());
    }

    /**
     * 手动刷新四圣挑战区域
     * @param uid
     * @param challengeType
     */
    public void manualRefreshFourSaintsChallenge(long uid, Integer challengeType){
        //获取玩家的四圣区域的攻打记录
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(uid, challengeType);
        userZxzFourSaints.refreshFourSaints();
        userZxzFourSaints.reduceFreeRefreshFrequency();
        gameUserService.updateItem(userZxzFourSaints);
    }

    /**
     * 判断是否刷新
     * @param lastRefreshDate
     * @param weekDay
     * @param refreshHour
     * @return
     */
    private static boolean ifRefresh(Date lastRefreshDate, int weekDay, int refreshHour){
        //这个星期以前
        if (!DateUtil.isThisWeek(lastRefreshDate)) {
            return true;
        }
        Date now = DateUtil.now();
        //本星期不同一天
        int nowWeekDay = DateUtil.getWeekDay(now);
        int lastWeekDay = DateUtil.getWeekDay(lastRefreshDate);
        int nowHour = DateUtil.getHourOfDay(now);
        int lastHour = DateUtil.getHourOfDay(lastRefreshDate);


        if (nowWeekDay > weekDay && lastWeekDay < weekDay) {
            return true;
        }
        if (nowWeekDay > weekDay && lastWeekDay == weekDay) {
            if (lastHour < refreshHour) {
                return true;
            }
        }
        if (nowWeekDay == weekDay && lastWeekDay < weekDay) {
            if (nowHour >= refreshHour) {
                return true;
            }
        }
        //本星期同一天
        if (nowWeekDay == weekDay && nowWeekDay == lastWeekDay) {
            if (nowHour >= refreshHour && lastHour < refreshHour) {
                return true;
            }
        }
        return false;
    }

}