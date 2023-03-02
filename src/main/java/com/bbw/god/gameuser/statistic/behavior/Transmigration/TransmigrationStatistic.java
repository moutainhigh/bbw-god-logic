package com.bbw.god.gameuser.statistic.behavior.Transmigration;

import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bbw.god.gameuser.statistic.StatisticConst.*;
import static com.bbw.god.gameuser.statistic.StatisticConst.TOTAL;

/**
 * 轮回世界统计
 *
 * @author fzj
 * @date 2021/9/17 17:25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TransmigrationStatistic extends BehaviorStatistic {
    public static String FIELD_NEW_RECORD = "newRecordNum";
    public static String FIELD_HIGH_SCORE = "highScoreNum";

    /** 某轮挑战成功的城池数 */
    private Map<String, Integer> successNumPerTransmigration = new HashMap<>();
    /** 某城累计挑战成功数 */
    private Map<String, Integer> successPerCity = new HashMap<>();
    /** 新纪录数 */
    private Integer newRecordNum;
    /** 高分数 */
    private Integer highScoreNum;

    public TransmigrationStatistic() {
        super(BehaviorType.TRANSMIGRATION_CHALLENGE);
    }

    public TransmigrationStatistic(int date, Map<String, Integer> redisMap) {
        setBehaviorType(BehaviorType.TRANSMIGRATION_CHALLENGE);
        String dateNumStr = date + UNDERLINE + NUM;
        Integer today = redisMap.get(dateNumStr) == null ? 0 : redisMap.get(dateNumStr);
        Integer total = redisMap.get(TOTAL) == null ? 0 : redisMap.get(TOTAL);
        setToday(today);
        setTotal(total);

        //某轮挑战成功统计
        String beginHms = TransmigrationTool.getCfg().getBeginHms();
        for (String field : redisMap.keySet()) {
            if (field.endsWith(beginHms)) {
                successNumPerTransmigration.put(field, redisMap.getOrDefault(field, 0));
            }
        }
        //某城累计挑战成功统计
        List<CfgCityEntity> cities = CityTool.getCities().stream()
                .filter(cfgCityEntity -> cfgCityEntity.getLevel() == 5).collect(Collectors.toList());
        for (CfgCityEntity city : cities) {
            successPerCity.put(city.getName(), redisMap.getOrDefault(city.getName(), 0));
        }
        //新纪录统计
        newRecordNum = redisMap.getOrDefault(FIELD_NEW_RECORD, 0);
        //获得100及以上分数统计
        highScoreNum = redisMap.getOrDefault(FIELD_HIGH_SCORE, 0);
    }
}
