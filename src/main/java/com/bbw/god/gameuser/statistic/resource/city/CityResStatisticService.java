package com.bbw.god.gameuser.statistic.resource.city;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.BaseStatistic;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;

/**
 * 城池统计service
 *
 * @author suchaobin
 * @date 2020/4/16 8:53
 **/
@Service
public class CityResStatisticService extends ResourceStatisticService {
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
        return AwardEnum.CITY;
    }

    /**
     * 将统计数据持久化到redis
     *
     * @param uid       玩家id
     * @param statistic 统计对象
     */
    @Override
    public <T extends BaseStatistic> void toRedis(long uid, T statistic) {
        if (!(statistic instanceof CityStatistic)) {
            throw CoderException.high("参数类型错误！");
        }
        CityStatistic cityStatistic = (CityStatistic) statistic;
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
        map.put(LEVEL + UNDERLINE + 5, cityStatistic.getAllFiveLevelCity());
        map.put(LEVEL + UNDERLINE + 6, cityStatistic.getAllSixLevelCity());
        map.put(LEVEL + UNDERLINE + 7, cityStatistic.getAllSevenLevelCity());
        map.put(LEVEL + UNDERLINE + 8, cityStatistic.getAllEightLevelCity());
        map.put(LEVEL + UNDERLINE + 9, cityStatistic.getAllNineLevelCity());
        map.put(LEVEL + UNDERLINE + 10, cityStatistic.getAllTenLevelCity());
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
        return (T) getCityStatistic(typeEnum, date, redisMap);
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
    public CityStatistic fromRedis(long uid, StatisticTypeEnum typeEnum, int date) {
        Map<String, Integer> redisMap = redisHashUtil.get(getKey(uid, typeEnum));
        return getCityStatistic(typeEnum, date, redisMap);
    }

    private CityStatistic getCityStatistic(StatisticTypeEnum typeEnum, int date, Map<String, Integer> redisMap) {
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
        Integer fiveLevel = redisMap.get(LEVEL_5) == null ? 0 : redisMap.get(LEVEL_5);
        Integer sixLevel = redisMap.get(LEVEL_6) == null ? 0 : redisMap.get(LEVEL_6);
        Integer sevenLevel = redisMap.get(LEVEL_7) == null ? 0 : redisMap.get(LEVEL_7);
        Integer eightLevel = redisMap.get(LEVEL_8) == null ? 0 : redisMap.get(LEVEL_8);
        Integer nineLevel = redisMap.get(LEVEL_9) == null ? 0 : redisMap.get(LEVEL_9);
        Integer tenLevel = redisMap.get(LEVEL_10) == null ? 0 : redisMap.get(LEVEL_10);
        return new CityStatistic(today, total, date, typeEnum.getValue(), oneStar, twoStar, threeStar, fourStar, fiveStar, gold, wood, water, fire, earth, fiveLevel, sixLevel, sevenLevel, eightLevel, nineLevel, tenLevel);
    }

    /**
     * 新增城池
     *
     * @param uid         玩家id
     * @param cityLevel   城池等级
     * @param cityCountry 城池所属国家
     */
    public void addCity(long uid, int cityLevel, int cityCountry) {
        int todayInt = DateUtil.getTodayInt();
        CityStatistic statistic = fromRedis(uid, StatisticTypeEnum.GAIN, todayInt);

        StringBuilder sb = new StringBuilder();
        sb.append("\nuid=").append(uid).append("城池统计数量异常！");
        sb.append("\nuid=").append(uid).append(" date=").append(todayInt).append(" cityLevel=").append(cityLevel).append(" cityCountry=").append(cityCountry);
        sb.append("\n添加前数据：").append(JSONUtil.toJson(statistic));

        boolean b = statistic.addCity(cityLevel, cityCountry);

        if (!b) {
            sb.append("\n添加后数据：").append(JSONUtil.toJson(statistic));
            this.init(uid);
            statistic = fromRedis(uid, StatisticTypeEnum.GAIN, todayInt);
            sb.append("\n修正后数据：").append(JSONUtil.toJson(statistic));
            logger.error(sb.toString());
        }

        toRedis(uid, statistic);
    }

    /**
     * 城内建筑物全部到达新等级
     *
     * @param newLevel 新等级
     */
    public void addAllLevelCity(long uid, int newLevel) {
        CityStatistic statistic = fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
        statistic.addAllLevelCity(newLevel);
        toRedis(uid, statistic);
    }

    /**
     * 初始化统计数据
     *
     * @param uid 玩家id
     */
    @Override
    public void init(long uid) {
        List<UserCity> userCities = userCityService.getUserOwnCities(uid);
        int cityLevel1 = userCityService.getOwnCityNumAsLevel(uid, 1);
        int cityLevel2 = userCityService.getOwnCityNumAsLevel(uid, 2);
        int cityLevel3 = userCityService.getOwnCityNumAsLevel(uid, 3);
        int cityLevel4 = userCityService.getOwnCityNumAsLevel(uid, 4);
        int cityLevel5 = userCityService.getOwnCityNumAsLevel(uid, 5);
        int goldCountryCity = userCityService.getOwnCityNumAsCountry(uid, 10);
        int woodCountryCity = userCityService.getOwnCityNumAsCountry(uid, 20);
        int waterCountryCity = userCityService.getOwnCityNumAsCountry(uid, 30);
        int fireCountryCity = userCityService.getOwnCityNumAsCountry(uid, 40);
        int earthCountryCity = userCityService.getOwnCityNumAsCountry(uid, 50);
        int allBuildingLevel5 = userCityService.getCityUpdateToLevelNum(uid, 5);
        int allBuildingLevel6 = userCityService.getCityUpdateToLevelNum(uid, 6);
        int allBuildingLevel7 = userCityService.getCityUpdateToLevelNum(uid, 7);
        int allBuildingLevel8 = userCityService.getCityUpdateToLevelNum(uid, 8);
        int allBuildingLevel9 = userCityService.getCityUpdateToLevelNum(uid, 9);
        int allBuildingLevel10 = userCityService.getCityUpdateToLevelNum(uid, 10);
        CityStatistic statistic = new CityStatistic(userCities.size(), userCities.size(), DateUtil.getTodayInt(), StatisticTypeEnum.GAIN.getValue(), cityLevel1, cityLevel2, cityLevel3, cityLevel4, cityLevel5, goldCountryCity, woodCountryCity, waterCountryCity, fireCountryCity, earthCountryCity, allBuildingLevel5, allBuildingLevel6, allBuildingLevel7, allBuildingLevel8, allBuildingLevel9, allBuildingLevel10);
        toRedis(uid, statistic);
    }
}
