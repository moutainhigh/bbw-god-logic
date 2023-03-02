package com.bbw.god.activity.holiday.lottery;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 节日抽奖工具
 * @date 2020/8/27 14:57
 **/
public class HolidayLotteryTool {

    private static List<CfgHolidayLotteryAwards> getAll() {
        return Cfg.I.get(CfgHolidayLotteryAwards.class);
    }

    public static List<CfgHolidayLotteryAwards> getAll(int type) {
        return getAll().stream().filter(tmp -> tmp.getType().equals(type)).collect(Collectors.toList());
    }

    public static CfgHolidayLotteryAwards getById(Integer id) {
        return Cfg.I.get(id, CfgHolidayLotteryAwards.class);
    }

    public static CfgHolidayLotteryAwards getByRandom(int type) {
        int random = PowerRandom.getRandomBySeed(10000);
        int sum = 0;
        List<CfgHolidayLotteryAwards> lotteryAwards = getAll(type);
        for (CfgHolidayLotteryAwards lotteryAward : lotteryAwards) {
            sum += lotteryAward.getProp();
            if (random <= sum) {
                return lotteryAward;
            }
        }
        return PowerRandom.getRandomFromList(lotteryAwards);
    }

    public static CfgHolidayLotteryAwards getByAwardId(int type, int awardId) {
        return getAll(type).stream().filter(tmp -> tmp.getAwards().get(0).getAwardId()
                .equals(awardId)).findFirst().orElse(null);
    }

    public static CfgHolidayLotteryAwards getByAwardLevel(int type, int level) {
        List<CfgHolidayLotteryAwards> cfgHolidayLotteryAwards = getAll(type).stream().filter(tmp -> tmp.getLevel() <= level).collect(Collectors.toList());
        return cfgHolidayLotteryAwards.stream().sorted(Comparator.comparing(CfgHolidayLotteryAwards::getLevel).reversed()).findFirst().orElse(null);
    }
}
