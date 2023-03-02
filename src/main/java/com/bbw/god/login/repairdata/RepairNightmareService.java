package com.bbw.god.login.repairdata;

import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.db.pool.StatisticPool;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementTool;
import com.bbw.god.gameuser.achievement.CfgAchievementEntity;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.nightmarecity.NightmareCityResStatisticService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.REST_NIGHTMARE_SETTLE;
import static com.bbw.god.login.repairdata.RepairDataConst.SEND_NIGHTMARE_AWARD;

/**
 * @author suchaobin
 * @description 梦魇相关修复
 * @date 2021/1/15 12:46
 **/
@Service
public class RepairNightmareService implements BaseRepairDataService {
    @Autowired
    private NightmareCityResStatisticService statisticService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    protected StatisticPool statisticPool;
    @Autowired
    protected UserAttackDifficultyLogic attackDifficultyLogic;

    /**
     * 梦魇相关成就
     */
    public static final List<Integer> achievementIds = Arrays
            .asList(14740, 14750, 14760, 14770, 14780, 14790, 14800, 14810, 14820, 14830, 14840, 14850, 14860, 14870, 14880, 14890);

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        //开启新的梦魇世界:清空旧数据，清除成就，添加神物
        long uid = gu.getId();
        if (lastLoginDate.before(SEND_NIGHTMARE_AWARD)) {
            cleanOldNightmareCityData(uid);
            cleanOldNightmareCityAchievement(uid);
            cleanOldNightmareCityStatistic(uid);
            addShenWu(uid);
        } else if (lastLoginDate.before(REST_NIGHTMARE_SETTLE)) {
            reSettleNightmare(uid);
        }
    }

    /**
     * 清空梦魇攻城记录
     *
     * @param uid
     */
    public void cleanOldNightmareCityData(long uid) {
        List<UserNightmareCity> nightmareCities = userCityService.getUserNightmareCities(uid);
        if (ListUtil.isNotEmpty(nightmareCities)) {
            for (UserNightmareCity nightmareCity : nightmareCities) {
                nightmareCity.reset();
            }
            gameUserService.updateItems(nightmareCities);
            UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(uid);
            if (difficulty != null) {
                int[] nightmare = {0, 0, 0, 0, 0};
                difficulty.setOwnNightMareCityNum(nightmare);
                gameUserService.updateItem(difficulty);
            }
        }
    }

    /**
     * 清除梦魇统计:删除redis数据=》重新生成空数据=》更新到数据库
     * 因为统计没有写同时删除Redis和MySQL的方法所以采用该方式删除
     *
     * @param uid
     */
    public void cleanOldNightmareCityStatistic(long uid) {
        String key1 = statisticService.getKey(uid, StatisticTypeEnum.GAIN);
        String key2 = statisticService.getKey(uid, StatisticTypeEnum.CONSUME);
        redisHashUtil.delete(key1);
        redisHashUtil.delete(key2);
        statisticService.init(uid);
    }

    /**
     * 清理所有梦魇城池相关的成就
     *
     * @param uid
     */
    public void cleanOldNightmareCityAchievement(long uid) {
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        if (info == null) {
            return;
        }
        info.clearAchievement(achievementIds);
        gameUserService.updateItem(info);
    }

    /**
     * 获取所有梦魇城池成就的名称
     *
     * @return
     */
    public List<String> getAllNightmareCityAchievementName() {
        List<String> names = new ArrayList<>();
        List<CfgAchievementEntity> achievements = AchievementTool.getAllAchievements();
        for (CfgAchievementEntity achievement : achievements) {
            if (achievementIds.contains(achievement.getId())) {
                names.add(achievement.getName());
            }
        }
        return names;
    }

    /**
     * 添加神物
     *
     * @param uid
     */
    public void addShenWu(long uid) {
        List<UserCity> ownCities = userCityService.getUserOwnCities(uid);
//        【神物-白虎牙】
//        道具描述：攻下西岐获得的战利品，拿在手上总让人心慌，不知作何用途。
//        获取条件：攻下封神大陆主城-西岐后，战斗奖励中附赠
        if (ownCities.stream().filter(p -> p.getBaseId() == 4325).findFirst().isPresent()) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHEN_WU_BAI_HU.getValue(), 1, WayEnum.LOGIN_REPAIR, new RDCommon());
        }
//        【神物-青龙鳞】
//        道具描述：攻下东鲁获得的战利品，拿在手上总让人心慌，不知作何用途。
//        获取条件：攻下封神大陆主城-东鲁后，战斗奖励中附赠
        if (ownCities.stream().filter(p -> p.getBaseId() == 1024).findFirst().isPresent()) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHEN_WU_QING_LONG.getValue(), 1, WayEnum.LOGIN_REPAIR, new RDCommon());
        }
//        【神物-朱雀羽】
//        道具描述：攻下南都获得的战利品，拿在手上总让人心慌，不知作何用途。
//        获取条件：攻下封神大陆主城-南都后，战斗奖励中附赠
        if (ownCities.stream().filter(p -> p.getBaseId() == 2608).findFirst().isPresent()) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHEN_WU_ZHU_QUE.getValue(), 1, WayEnum.LOGIN_REPAIR, new RDCommon());
        }
//        【神物-玄武壳】
//        道具描述：攻下曹州获得的战利品，拿在手上总让人心慌，不知作何用途。
//        获取条件：攻下封神大陆主城-曹州后，战斗奖励中附赠
        if (ownCities.stream().filter(p -> p.getBaseId() == 2539).findFirst().isPresent()) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHEN_WU_XUAN_WU.getValue(), 1, WayEnum.LOGIN_REPAIR, new RDCommon());
        }
//        【神物-麒麟角】
//        道具描述：攻下朝歌获得的战利品，拿在手上总让人心慌，不知作何用途。
//        获取条件：攻下封神大陆主城-朝歌后，战斗奖励中附赠
        if (ownCities.stream().filter(p -> p.getBaseId() == 2725).findFirst().isPresent()) {
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SHEN_WU_QI_LIN.getValue(), 1, WayEnum.LOGIN_REPAIR, new RDCommon());
        }
    }

    /**
     * 重新统计城池难度
     *
     * @param uid
     */
    public void reSettleNightmare(long uid) {
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(uid);
        if (difficulty != null) {
            int[] nightmare = {0, 0, 0, 0, 0};
            for (int i = 1; i <= 5; i++) {
                int numAsLevel = userCityService.getOwnNightmareCityNumAsLevel(uid, i);
                nightmare[i - 1] = numAsLevel;
            }
            difficulty.setOwnNightMareCityNum(nightmare);
            gameUserService.updateItem(difficulty);
        }
    }
}
