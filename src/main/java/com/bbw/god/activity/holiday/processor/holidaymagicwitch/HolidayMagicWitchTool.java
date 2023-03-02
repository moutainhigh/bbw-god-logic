package com.bbw.god.activity.holiday.processor.holidaymagicwitch;

import com.bbw.god.game.config.Cfg;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 魔法女巫工具类
 *
 * @author: huanghb
 * @date: 2022/12/14 14:34
 */
public class HolidayMagicWitchTool {
    /**
     * 获得配置类
     *
     * @return
     */
    public static CfgHolidayMagicWitch getCfgHolidayMagicWoman() {
        return Cfg.I.getUniqueConfig(CfgHolidayMagicWitch.class);
    }

    /**
     * 获得奖励分布结果信息
     *
     * @return
     */
    public static List<DrawResult> getAwardResults() {
        return getCfgHolidayMagicWoman().getAwardResults();
    }


    /**
     * 获得消耗道具信息
     *
     * @return
     */
    public static List<CfgHolidayMagicWitch.DrawConsumption> getCurrentRoundDarwConsumption() {
        return getCfgHolidayMagicWoman().getDrawConsumptions();
    }

    /**
     * 获得基础材料
     *
     * @return
     */
    public static List<Integer> getBaseMaterial() {
        return getCfgHolidayMagicWoman().getBaseMaterial();
    }

    /**
     * 获得奖励结果数量
     *
     * @return
     */
    public static Integer getResultSize() {
        return getCfgHolidayMagicWoman().getResultSize();
    }

    /**
     * 根据等级获得奖励
     *
     * @param level
     * @return
     */
    public static HolidayMagicWitchAward getByAwardLevel(int level) {
        List<HolidayMagicWitchAward> holidayMagicWitchAwards = getCfgHolidayMagicWoman().getAwardPool().stream().filter(tmp -> tmp.getLevel() <= level).collect(Collectors.toList());
        return holidayMagicWitchAwards.stream().sorted(Comparator.comparing(HolidayMagicWitchAward::getLevel).reversed()).findFirst().orElse(null);
    }


}
