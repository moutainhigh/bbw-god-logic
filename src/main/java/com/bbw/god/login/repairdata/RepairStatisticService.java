package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.card.DrawCardStatistic;
import com.bbw.god.gameuser.statistic.event.StatisticEventPublisher;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.gameuser.statistic.resource.card.CardResStatisticService;
import com.bbw.god.gameuser.statistic.resource.card.CardStatistic;
import com.bbw.god.gameuser.statistic.resource.city.CityResStatisticService;
import com.bbw.god.gameuser.statistic.resource.city.CityStatistic;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.*;

/**
 * @author suchaobin
 * @description 修复统计数据
 * @date 2020/7/7 15:22
 **/
@Service
public class RepairStatisticService implements BaseRepairDataService {
    @Autowired
    private StatisticServiceFactory statisticServiceFactory;
    @Autowired
    private RedisHashUtil<String, Object> redisHashUtil;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private CityResStatisticService cityResStatisticService;
    @Autowired
    private CardResStatisticService cardResStatisticService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        // 统计值的初始化
        if (lastLoginDate.before(STATISTIC_INIT_TIME)) {
            statisticServiceFactory.init(gu.getId());
        }
        // 充值统计初始化
        if (lastLoginDate.before(RECHARGE_STATISTIC_INIT_TIME)) {
            BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.RECHARGE);
            service.init(gu.getId());
        }
        // 城池和卡牌重新初始化
        if (lastLoginDate.before(REINIT_CITY_CARD_STATISTIC_TIME)) {
            // 城池统计检查，不对就重新初始化
            checkCityStatistic(gu.getId());
            // 卡牌统计检查，不对就重新初始化
            checkCardStatistic(gu.getId());
        }
        // 重新初始化抽卡统计
        if (lastLoginDate.before(REINIT_DRAW_CARD_STATISTIC_TIME)) {
            BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.CARD_DRAW);
            DrawCardStatistic statistic = service.fromRedis(gu.getId(), StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            if (statistic.getTotal() > 0 && 0 == statistic.getGold()) {
                int value = statistic.getTotal() / 6;
                statistic.setGold(value);
                statistic.setWood(value);
                statistic.setWater(value);
                statistic.setFire(value);
                statistic.setEarth(value);
                statistic.setWanwu(value);
                service.toRedis(gu.getId(), statistic);
            }
        }
        // 重新初始化梦魇城池统计
        if (lastLoginDate.before(REINIT_NIGHTMARE_STATISTIC_TIME)) {
            userCityService.delUserNightmareCities(gu.getId());
            reInitResStatistic(gu.getId(), AwardEnum.NIGHTMARE_CITY);
        }
        //重新初始化碧游宫秘传统计
        if (lastLoginDate.before(REINIT_BIYOU_STATISTIC_TIME)) {
            reInitBehaviorStatistic(gu.getId(), BehaviorType.BI_YOU);
        }
        //重新初始化封神台统计
        if (lastLoginDate.before(REINIT_FST_STATISTIC_TIME)) {
            reInitBehaviorStatistic(gu.getId(), BehaviorType.FST);
        }
        //重新初始化主角卡装备统计
        if (lastLoginDate.before(REINIT_EQUIPMENT_STATISTIC_TIME)) {
            reInitBehaviorStatistic(gu.getId(), BehaviorType.LEADER_EQUIPMENT);
        }
    }

    /**
     * 检查城池统计,错误则重新初始化
     *
     * @param uid
     */
    private void checkCityStatistic(long uid) {
        CityStatistic statistic = cityResStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        int num5 = userCityService.getCityUpdateToLevelNum(uid, 5);
        int num6 = userCityService.getCityUpdateToLevelNum(uid, 6);
        int num7 = userCityService.getCityUpdateToLevelNum(uid, 7);
        int num8 = userCityService.getCityUpdateToLevelNum(uid, 8);
        int num9 = userCityService.getCityUpdateToLevelNum(uid, 9);
        int num10 = userCityService.getCityUpdateToLevelNum(uid, 10);
        int total = userCityService.getUserOwnCities(uid).size();
        if (total != statistic.getTotal()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num5 != statistic.getAllFiveLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num6 != statistic.getAllSixLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num7 != statistic.getAllSevenLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num8 != statistic.getAllEightLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num9 != statistic.getAllNineLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        } else if (num10 != statistic.getAllTenLevelCity()) {
            reInitResStatistic(uid, AwardEnum.CITY);
        }
    }

    /**
     * 检查卡牌统计,错误则重新初始化
     *
     * @param uid
     */
    private void checkCardStatistic(long uid) {
        CardStatistic statistic = cardResStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        int size = userCardService.getUserCards(uid).size();
        if (size != statistic.getTotal()) {
            reInitResStatistic(uid, AwardEnum.KP);
        }
    }

    /**
     * 重新初始化资源统计数据
     *
     * @param uid       玩家id
     * @param awardEnum 资源枚举
     */
    public void reInitResStatistic(long uid, AwardEnum awardEnum) {
        ResourceStatisticService service = statisticServiceFactory.getByAwardEnum(awardEnum);
        String gainKey = service.getKey(uid, StatisticTypeEnum.GAIN);
        String consumeKey = service.getKey(uid, StatisticTypeEnum.CONSUME);
        redisHashUtil.delete(gainKey);
        redisHashUtil.delete(consumeKey);
        service.init(uid);
        ResourceStatistic gainStatistic = service.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        ResourceStatistic consumeStatistic = service.fromRedis(uid, StatisticTypeEnum.CONSUME, DateUtil.getTodayInt());
        StatisticEventPublisher.pubResourceStatisticEvent(uid, WayEnum.LOGIN_REPAIR, new RDCommon(), gainStatistic);
        StatisticEventPublisher.pubResourceStatisticEvent(uid, WayEnum.LOGIN_REPAIR, new RDCommon(), consumeStatistic);
    }

    /**
     * 重新初始化行为统计数据
     *
     * @param uid          玩家id
     * @param behaviorType 行为枚举
     */
    public void reInitBehaviorStatistic(long uid, BehaviorType behaviorType) {
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(behaviorType);
        String key = service.getKey(uid, StatisticTypeEnum.NONE);
        redisHashUtil.delete(key);
        service.init(uid);
        BehaviorStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        StatisticEventPublisher.pubBehaviorStatisticEvent(uid, WayEnum.LOGIN_REPAIR, new RDCommon(), statistic);
    }
}
