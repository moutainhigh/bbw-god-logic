package com.bbw.god.activity.holiday.lottery;

import com.bbw.god.game.config.Cfg;

import java.util.List;

/**
 * 节五气朝元活动参数配置工具
 *
 * @author: huanghb
 * @date: 2022/5/3 15:58
 */
public class HolidayWQCYTool {
    /**
     * 获得五气朝元活动参数配置类
     *
     * @return
     */
    public static CfgHolidayWQCY getWQCYCfg() {
        return Cfg.I.getUniqueConfig(CfgHolidayWQCY.class);
    }


    /**
     * 获得当前轮次消耗道具信息
     *
     * @return
     */
    public static List<CfgHolidayWQCY.DrawConsumption> getCurrentRoundDarwConsumption() {
        return getWQCYCfg().getDrawConsumptions();
    }

    /**
     * 获得第一个奖励结果
     *
     * @return
     */
    public static List<Integer> getFirstRewardResult() {
        return getWQCYCfg().getFirstRewardResult();
    }

    /**
     * 特产产出总概率
     *
     * @return
     */
    public static Integer getSpecialsOutPutTotalProb() {
        return getWQCYCfg().getSpecialsOutPutTotalProb();
    }

    /**
     * 特产产出一个圣元珠概率
     *
     * @return
     */
    public static Integer getSpecialsOutPutOneSYZProb() {
        return getWQCYCfg().getSpecialsOutPutOneSYZProb();
    }

    /**
     * 特产产出两个圣元珠概率
     *
     * @return
     */
    public static Integer getSpecialsOutPutTwoSYZProb() {
        return getSpecialsOutPutOneSYZProb() + getWQCYCfg().getSpecialsOutPutTwoSYZProb();
    }

    /**
     * 获得所有抽奖轮次信息
     *
     * @return
     */
    public static List<CfgHolidayWQCY.DrawRoundInfo> getAllDrawRoundInfos() {
        return getWQCYCfg().getDrawRoundInfos();
    }

    /**
     * 获得当前抽奖轮次信息
     *
     * @return
     */
    public static CfgHolidayWQCY.DrawRoundInfo getCurrentDrawRoundInfos(Integer currentRound) {
        return getWQCYCfg().getDrawRoundInfos().stream().filter(tmp -> tmp.getMinRound() >= currentRound).findFirst().orElse(null);
    }

}
