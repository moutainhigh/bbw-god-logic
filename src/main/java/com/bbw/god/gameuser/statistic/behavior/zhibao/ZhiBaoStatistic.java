package com.bbw.god.gameuser.statistic.behavior.zhibao;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

import static com.bbw.god.gameuser.statistic.StatisticConst.NUM;
import static com.bbw.god.gameuser.statistic.StatisticConst.UNDERLINE;

/**
 * 至宝获得行为统计
 *
 * @author: huanghb
 * @date: 2022/9/27 10:27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ZhiBaoStatistic extends BehaviorStatistic {
    public static String FAIRY_FAQI_NUM = "fairyFaQiNum";
    public static String FAIRY_LINGBAO_NUM = "fairyLingBaoNum";
    public static String GOLD_PROPERTY_FAQI_NUM = "goldPropertyFaQiNum";
    public static String GOLD_PROPERTY_LINGBAO_NUM = "goldPropertyLingBaoNum";
    public static String WOOD_PROPERTY_FAQI_NUM = "woodPropertyFaQiNum";
    public static String WOOD_PROPERTY_LINGBAO_NUM = "woodPropertyLingBaoNum";
    public static String WATER_PROPERTY_FAQI_NUM = "waterPropertyFaQiNum";
    public static String WATER_PROPERTY_LINGBAO_NUM = "waterPropertyLingBaoNum";
    public static String FIRE_PROPERTY_FAQI_NUM = "firePropertyFaQiNum";
    public static String FIRE_PROPERTY_LINGBAO_NUM = "firePropertyLingBaoNum";
    public static String EARTH_PROPERTY_FAQI_NUM = "earthPropertyFaQiNum";
    public static String EARTH_PROPERTY_LINGBAO_NUM = "earthPropertyLingBaoNum";
    public static String ZHIBAO_NUM = "zhiBaoNum";
    public static String FULL_ATTACK_NUM = "fullAttackNum";
    public static String FULL_DEFENSE_NUM = "fullDefenseNum";
    /** 极品法器数量 */
    private Integer fairyFaQiNum = 0;
    /** 极品灵宝数量 */
    private Integer fairyLingBaoNum = 0;
    /** 金属性法器数量 */
    private Integer goldPropertyFaQiNum = 0;
    /** 金属性灵宝数量 */
    private Integer goldPropertyLingBaoNum = 0;
    /** 木属性法器数量 */
    private Integer woodPropertyFaQiNum = 0;
    /** 木属性灵宝数量 */
    private Integer woodPropertyLingBaoNum = 0;
    /** 水属性法器数量 */
    private Integer waterPropertyFaQiNum = 0;
    /** 水属性灵宝数量 */
    private Integer waterPropertyLingBaoNum = 0;
    /** 火属性法器数量 */
    private Integer firePropertyFaQiNum = 0;
    /** 火属性灵宝数量 */
    private Integer firePropertyLingBaoNum = 0;
    /** 土属性法器数量 */
    private Integer earthPropertyFaQiNum = 0;
    /** 土属性灵宝数量 */
    private Integer earthPropertyLingBaoNum = 0;
    /** 至宝数量 */
    private Integer zhiBaoNum = 0;
    /** 满攻数量 */
    private Integer fullAttackNum = 0;
    /** 满防数量 */
    private Integer fullDefenseNum = 0;


    public ZhiBaoStatistic() {
        super(BehaviorType.KUNLS_INFUSION);
    }

    /**
     * 至宝获得统计
     *
     * @param date
     * @param redisMap
     */
    public ZhiBaoStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.PINCH_PEOPLE);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.getOrDefault(dateNumStr, 0);
        setToday(today);
        goldPropertyFaQiNum = redisMap.getOrDefault(GOLD_PROPERTY_FAQI_NUM, 0);
        goldPropertyLingBaoNum = redisMap.getOrDefault(GOLD_PROPERTY_LINGBAO_NUM, 0);
        woodPropertyFaQiNum = redisMap.getOrDefault(WOOD_PROPERTY_FAQI_NUM, 0);
        woodPropertyLingBaoNum = redisMap.getOrDefault(WOOD_PROPERTY_LINGBAO_NUM, 0);
        waterPropertyFaQiNum = redisMap.getOrDefault(WATER_PROPERTY_FAQI_NUM, 0);
        waterPropertyLingBaoNum = redisMap.getOrDefault(WATER_PROPERTY_LINGBAO_NUM, 0);
        firePropertyFaQiNum = redisMap.getOrDefault(FIRE_PROPERTY_FAQI_NUM, 0);
        firePropertyLingBaoNum = redisMap.getOrDefault(FIRE_PROPERTY_LINGBAO_NUM, 0);
        earthPropertyFaQiNum = redisMap.getOrDefault(EARTH_PROPERTY_FAQI_NUM, 0);
        earthPropertyLingBaoNum = redisMap.getOrDefault(EARTH_PROPERTY_LINGBAO_NUM, 0);
        fairyFaQiNum = redisMap.getOrDefault(FAIRY_FAQI_NUM, 0);
        fairyLingBaoNum = redisMap.getOrDefault(FAIRY_LINGBAO_NUM, 0);
        zhiBaoNum = redisMap.getOrDefault(ZHIBAO_NUM, 0);
        fullAttackNum = redisMap.getOrDefault(FULL_ATTACK_NUM, 0);
        fullDefenseNum = redisMap.getOrDefault(FULL_DEFENSE_NUM, 0);

    }
}
