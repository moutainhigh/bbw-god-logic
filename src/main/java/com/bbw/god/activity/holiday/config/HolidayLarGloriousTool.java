package com.bbw.god.activity.holiday.config;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日劳动光荣工具类
 *
 * @author: huanghb
 * @date: 2022/11/28 11:12
 */
public class HolidayLarGloriousTool {
    /**
     * 获得配置
     *
     * @return
     */
    public static CfgHolidayLaborGlorious getCfg() {
        return Cfg.I.getUniqueConfig(CfgHolidayLaborGlorious.class);
    }

    /**
     * 特产产出一个特产概率
     *
     * @return
     */
    public static Integer getSpecialOutPutOneSpecialProb() {
        return getCfg().getSpecialsTriggerPro();
    }

    /**
     * 特产产出两个特产概率
     *
     * @return
     */
    public static Integer getSpecialOutPutTwoSpecialProb() {
        return getSpecialOutPutOneSpecialProb() + getCfg().getSpecialsTriggerDoublePro();
    }

    /**
     * 特产产出
     *
     * @return
     */
    public static List<Award> getSpecialsOutput() {
        return getCfg().getSpecialsOutput();
    }

    /**
     * 村庄事件产出
     *
     * @return
     */
    public static List<Award> getCunZEventBoxOutput() {
        return getCfg().getCunZEventBoxOutput();
    }

    /**
     * 事件总概率
     *
     * @return
     */
    public static Integer getEventTotalPro() {
        return getCfg().getEventTotalPro();
    }

    /**
     * 获得初始销售特产
     *
     * @return
     */
    public static List<CfgHolidayLaborGlorious.InitialSellingPrice> getInitialSellingPrice() {
        return getCfg().getInitialSellingPrice();
    }

    /**
     * 获得每次增加的价格
     *
     * @return
     */
    public static int getPricePerIncrease() {
        return getCfg().getPricePerIncrease();
    }

    /**
     * 根据概率随机获得奖励
     *
     * @param
     * @return
     */
    public static Award randomAwardByProb(List<Award> outPut) {
        List<Integer> probs = outPut.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return outPut.get(awardIndex);
    }
}
