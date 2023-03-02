package com.bbw.god.gm;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.achievement.AchievementStatusEnum;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatisticService;
import com.bbw.god.gameuser.yaozu.*;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bbw.god.gameuser.statistic.StatisticConst.TOTAL;

/**
 * 妖族来袭相关操作
 *
 * @author fzj
 * @date 2021/10/3 15:57
 */
@RestController
@RequestMapping("/gm/yaoZu/")
public class GMYaoZuCtrl extends AbstractController {
    private static final String ALL = "所有";
    /** 妖族相关成就 */
    private static final List<Integer> achievementIds = Arrays.asList(15740, 15690, 15700, 15710, 15720, 15730);
    @Autowired
    UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    ServerUserService serverUserService;
    @Autowired
    YaoZuStatisticService yaoZuStatisticService;
    @Autowired
    RedisHashUtil redisHashUtil;

    /**
     * 重置某个玩家某个妖族的进度,并更新对应统计数据和成就
     *
     * @param username
     * @param yaoZuName 刃纹·野猪妖  所有
     * @return
     */
    @RequestMapping("user!resetYaoZu")
    public Rst resetYaoZuData(int sid, String username, String yaoZuName) {
        Long uid = serverUserService.getUidByNickName(sid, username).get();
        if (yaoZuName.equals(ALL)) {
            return clearAllYaoZuStatisticAndAchievement(uid);
        }
        List<CfgYaoZuEntity> cfgYaoZuList = YaoZuTool.getAllYaoZu().stream()
                .filter(YaoZu -> yaoZuName.contains(YaoZu.getName())).collect(Collectors.toList());
        List<UserYaoZuInfo> yaoZuInfoList = new ArrayList<>();
        //重置进度
        for (CfgYaoZuEntity yaoZu : cfgYaoZuList) {
            UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZu.getYaoZuId());
            yaoZuInfo.setProgress(0);
            yaoZuInfoList.add(yaoZuInfo);
        }
        updateYaoZuStatisticAndAchievement(uid, yaoZuInfoList);
        gameUserService.updateItems(yaoZuInfoList);
        return Rst.businessOK();
    }

    /**
     * 更新妖族对应统计数据和对应成就
     *
     * @param uid
     * @param yaoZuInfoList
     */
    public void updateYaoZuStatisticAndAchievement(Long uid, List<UserYaoZuInfo> yaoZuInfoList) {
        if (yaoZuInfoList.isEmpty()) {
            return;
        }
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        String key = yaoZuStatisticService.getKey(uid, StatisticTypeEnum.NONE);
        List<Integer> accomplishedIds = new ArrayList<>();
        for (UserYaoZuInfo yaoZu : yaoZuInfoList) {
            int index = yaoZu.getYaoZuType() / 100;
            int achievementStatus = info.getAchievementStatus(achievementIds.get(index));
            if (achievementStatus != AchievementStatusEnum.NO_ACCOMPLISHED.getValue()) {
                accomplishedIds.add(achievementIds.get(index));
            }
            redisHashUtil.decrement(key, YaoZuEnum.fromValue(yaoZu.getYaoZuType()).getName(), 1);
        }
        if (ListUtil.isNotEmpty(accomplishedIds)) {
            List<Integer> accomplishList = accomplishedIds.stream().distinct().collect(Collectors.toList());
            info.clearAchievement(accomplishList);
        }
        redisHashUtil.decrement(key, TOTAL, yaoZuInfoList.size());
        gameUserService.updateItem(info);
    }

    /**
     * 清除某个玩家的所有妖族进度和统计信息,并重置所有妖族成就
     *
     * @param uid
     * @return
     */
    public Rst clearAllYaoZuStatisticAndAchievement(Long uid) {
        List<UserYaoZuInfo> yaoZuInfos = userYaoZuInfoService.getUserYaoZu(uid);
        if (ListUtil.isNotEmpty(yaoZuInfos)) {
            //重置进度
            for (UserYaoZuInfo yaoZu : yaoZuInfos) {
                yaoZu.setProgress(0);
            }
            gameUserService.updateItems(yaoZuInfos);
        }
        //清除统计数据
        String key = yaoZuStatisticService.getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.delete(key);
        //重置所有成就
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (info == null) {
            return Rst.businessFAIL("该玩家没有成就信息");
        }
        info.clearAchievement(achievementIds);
        gameUserService.updateItem(info);
        return Rst.businessOK();
    }
}
