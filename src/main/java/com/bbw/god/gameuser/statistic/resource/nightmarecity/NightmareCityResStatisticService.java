package com.bbw.god.gameuser.statistic.resource.nightmarecity;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.city.UserCityService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * @author suchaobin
 * @description 梦魇城池统计service
 * @date 2020/9/15 10:30
 **/
@Service
public class NightmareCityResStatisticService extends ResourceStatisticService {
    @Autowired
    private RedisHashUtil<String, Integer> redisHashUtil;
    @Autowired
    private UserCityService userCityService;

    /**
     * 获取类型总数，例：城池统计只有获得，没有消耗，返回1 元宝统计，有获得也有消耗，返回2
     *
     * @return 类型总数
     */
    @Override
    public int getMyTypeCount() {
        return 1;
    }

    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.NIGHTMARE_CITY;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof NightmareCityStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        NightmareCityStatistic cityStatistic = (NightmareCityStatistic) statistic;
        Integer date = cityStatistic.getDate();
        int type = cityStatistic.getType();
        Map<String, Integer> map = new HashMap<>(16);
        map.put(date + UNDERLINE + NUM, cityStatistic.getToday());
        map.put(TOTAL, cityStatistic.getTotal());
        map.put(STAR + UNDERLINE + 1, cityStatistic.getOneStarCity());
        map.put(STAR + UNDERLINE + 2, cityStatistic.getTwoStarCity());
        map.put(STAR + UNDERLINE + 3, cityStatistic.getThreeStarCity());
        map.put(STAR + UNDERLINE + 4, cityStatistic.getFourStarCity());
        map.put(STAR + UNDERLINE + 5, cityStatistic.getFiveStarCity());
        map.put(COUNTRY + UNDERLINE + 10, cityStatistic.getGoldCountryCity());
        map.put(COUNTRY + UNDERLINE + 20, cityStatistic.getWoodCountryCity());
        map.put(COUNTRY + UNDERLINE + 30, cityStatistic.getWaterCountryCity());
        map.put(COUNTRY + UNDERLINE + 40, cityStatistic.getFireCountryCity());
        map.put(COUNTRY + UNDERLINE + 50, cityStatistic.getEarthCountryCity());
        String key = getKey(uid, StatisticTypeEnum.fromValue(type));
        redisHashUtil.putAllField(key, map);
        checkUid(uid);
        statisticPool.toUpdatePool(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseStatistic> T buildStatisticFromPreparedData(long uid, StatisticTypeEnum typeEnum, int date, Map<String, Map<String, Object>> preparedData) {
        String key = getKey(uid, typeEnum);
        Map<String, Object> map = preparedData.get(key);
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
        return (T) getNightmareCityStatistic(typeEnum, date, redisMap);
    }

    /**
     * 从redis读取数据并转成统计对象
     *
     * @param uid      玩家id
     * @param typeEnum 类型枚举
     * @param date     日期
     * @return 统计对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public NightmareCityStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getNightmareCityStatistic(typeEnum, date, redisMap);
    }

    private NightmareCityStatistic getNightmareCityStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        Integer oneStar = redisMap.get(ONE_STAR) == null ? 0 : redisMap.get(ONE_STAR);
        Integer twoStar = redisMap.get(TWO_STAR) == null ? 0 : redisMap.get(TWO_STAR);
        Integer threeStar = redisMap.get(THREE_STAR) == null ? 0 : redisMap.get(THREE_STAR);
        Integer fourStar = redisMap.get(FOUR_STAR) == null ? 0 : redisMap.get(FOUR_STAR);
        Integer fiveStar = redisMap.get(FIVE_STAR) == null ? 0 : redisMap.get(FIVE_STAR);
        Integer gold = redisMap.get(COUNTRY_10) == null ? 0 : redisMap.get(COUNTRY_10);
        Integer wood = redisMap.get(COUNTRY_20) == null ? 0 : redisMap.get(COUNTRY_20);
        Integer water = redisMap.get(COUNTRY_30) == null ? 0 : redisMap.get(COUNTRY_30);
        Integer fire = redisMap.get(COUNTRY_40) == null ? 0 : redisMap.get(COUNTRY_40);
        Integer earth = redisMap.get(COUNTRY_50) == null ? 0 : redisMap.get(COUNTRY_50);
        return new NightmareCityStatistic(today, total, date, typeEnum.getValue(), oneStar, twoStar, threeStar, fourStar,
                fiveStar, gold, wood, water, fire, earth);
    }

    /**
     * 新增城池
     *
     * @param uid         玩家id
     * @param date        日期
     * @param cityLevel   城池等级
     * @param cityCountry 城池所属国家
     */
    public void addCity(long uid, int date, int cityLevel, int cityCountry) {
        NightmareCityStatistic statistic = fromRedis(uid, StatisticTypeEnum.GAIN, date);
        statistic.addCity(cityLevel, cityCountry);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        List<UserNightmareCity> userCities = userCityService.getUserOwnNightmareCities(uid);
        List<CfgCityEntity> cityEntities = new ArrayList<>();
        if (ListUtil.isNotEmpty(userCities)) {
            for (UserNightmareCity nightmareCity : userCities) {
                cityEntities.add(CityTool.getCityById(nightmareCity.getBaseId()));
            }
        }
        Long cityLevel1 = cityEntities.stream().filter(p -> p.getLevel() == 1).count();
        Long cityLevel2 = cityEntities.stream().filter(p -> p.getLevel() == 2).count();
        Long cityLevel3 = cityEntities.stream().filter(p -> p.getLevel() == 3).count();
        Long cityLevel4 = cityEntities.stream().filter(p -> p.getLevel() == 4).count();
        Long cityLevel5 = cityEntities.stream().filter(p -> p.getLevel() == 5).count();
        Long goldCountryCity = cityEntities.stream().filter(p -> p.getProperty() == 10).count();
        Long woodCountryCity = cityEntities.stream().filter(p -> p.getProperty() == 20).count();
        Long waterCountryCity = cityEntities.stream().filter(p -> p.getProperty() == 30).count();
        Long fireCountryCity = cityEntities.stream().filter(p -> p.getProperty() == 40).count();
        Long earthCountryCity = cityEntities.stream().filter(p -> p.getProperty() == 50).count();
        NightmareCityStatistic statistic = new NightmareCityStatistic(userCities.size(), userCities.size(),
                DateUtil.getTodayInt(), StatisticTypeEnum.GAIN.getValue(), cityLevel1.intValue(), cityLevel2.intValue(), cityLevel3.intValue(),
                cityLevel4.intValue(), cityLevel5.intValue(), goldCountryCity.intValue(), woodCountryCity.intValue(), waterCountryCity.intValue(), fireCountryCity.intValue(),
                earthCountryCity.intValue());
        toRedis(uid, statistic);
    }

}
