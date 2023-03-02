package com.bbw.god.city.chengc.in;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashion;
import com.bbw.god.gameuser.leadercard.fashion.UserLeaderFashionService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.fatan.FaTanStatistic;
import com.bbw.god.gameuser.yuxg.UserYuXGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 法坛服务类
 *
 * @author fzj
 * @date 2021/11/13 15:14
 */
@Service
public class FaTanService {
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    UserCityService userCityService;
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;
    @Autowired
    protected RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserYuXGService userYuXGService;


    /**
     * 获取法坛累计等级
     *
     * @param uid
     * @return
     */
    public int getTotalLevel(long uid) {
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.FA_TAN);
        FaTanStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        //计算绿茵时装等级加成
        int fashionLv = 0;
        UserLeaderFashion fashion = userLeaderFashionService.getFashion(uid, TreasureEnum.FASHION_LVYSZ.getValue());
        if (null != fashion) {
            fashionLv = fashion.getLevel();
        }
        int faTanLv = statistic.getTotalFaTanLv() + fashionLv;
        userYuXGService.levelUnlockFuCeNum(uid, faTanLv);
        return faTanLv;
    }

    /**
     * 法坛总等级概率加成
     *
     * @param uid
     * @return
     */
    public double addition(long uid) {
        int faTanTotalLevel = getTotalLevel(uid);
        if (faTanTotalLevel < 70) {
            return 1;
        }
        //只计算850等级的概率
        if (faTanTotalLevel >= 850) {
            faTanTotalLevel = 850;
        }
        int addition = (faTanTotalLevel - 70) / 20 + 1;
        return addition * 0.025 + 1;
    }

    /**
     * 修复法坛统计数据
     *
     * @param uid
     */
    public void repairFaTanStatistic(long uid) {
        //获取所有城池
        int totalFaTanLv = 0;
        int unlockFaTan = 0;
        List<UserCity> userCities = userCityService.getUserCities(uid);
        for (UserCity userCity : userCities) {
            Integer ft = userCity.getFt();
            if (null == ft) {
                continue;
            }
            totalFaTanLv += ft;
            unlockFaTan++;
        }
        if (totalFaTanLv == 0 && unlockFaTan == 0) {
            return;
        }
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.FA_TAN);
        String key = service.getKey(uid, StatisticTypeEnum.NONE);
        //全量统计
        service.increment(uid, DateUtil.getTodayInt(), 1);
        //法坛升级统计
        redisHashUtil.putField(key, FaTanStatistic.ALL_FATAN_LV, totalFaTanLv);
        //法坛解锁统计
        redisHashUtil.putField(key, FaTanStatistic.UNLOCK_FATAN_NUM, unlockFaTan);
    }

    /**
     * 获得修复后的法坛数据
     *
     * @param uid
     */
    public Integer getUserFaTanDaTa(long uid, List<UserCity> userCities) {
        int totalLevel = getTotalLevel(uid);
        //获取所有城池
        int totalFaTanLv = 0;
        for (UserCity userCity : userCities) {
            Integer ft = userCity.getFt();
            if (null == ft) {
                continue;
            }
            totalFaTanLv += ft;
        }
        if (totalLevel == totalFaTanLv) {
            return totalLevel;
        }
        repairFaTanStatistic(uid);
        return getTotalLevel(uid);
    }
}
