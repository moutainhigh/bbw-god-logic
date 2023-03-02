package com.bbw.god.rechargeactivities.processor.dailyshake;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgDailyShake;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日摇一摇配置类
 *
 * @author: huanghb
 * @date: 2022/6/16 14:09
 */
public class CfgDailyShakeTool {
    /**
     * 获得每日摇一摇配置
     *
     * @return
     */
    public static CfgDailyShake getCfgDailyShake() {
        return Cfg.I.getUniqueConfig(CfgDailyShake.class);
    }

    /**
     * 获得摇一摇次数上限
     *
     * @return
     */
    public static int getShakeTimesLimit() {
        return getCfgDailyShake().getShakeTimesLimit();
    }

    /**
     * 获得第一个奖池的奖励
     *
     * @param shakeTimes
     * @return
     */
    public static Award getFirstPrizePoolAwards(int shakeTimes) {
        List<CfgDailyShake.PrizePool> prizePools = getCfgDailyShake().getFirstPrizePools();
        int shakeTimesLimit = getShakeTimesLimit();
        final int finalShakeTimes = shakeTimes > shakeTimesLimit ? shakeTimesLimit : shakeTimes;
        CfgDailyShake.PrizePool prizePool = prizePools.stream().filter(tmp -> tmp.getShakeTimes() == finalShakeTimes).findFirst().orElse(null);
        List<Award> awards = prizePool.getAwards();
        List<Integer> prizePloolProbs = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(prizePloolProbs);
        return awards.get(awardIndex);
    }

    /**
     * 获得第二个奖池的奖励
     *
     * @param shakeTimes
     * @return
     */
    public static Award getSecondPrizePoolAwards(int shakeTimes) {
        List<CfgDailyShake.PrizePool> prizePools = getCfgDailyShake().getSecondPrizePools();
        int shakeTimesLimit = getShakeTimesLimit();
        final int finalShakeTimes = shakeTimes > shakeTimesLimit ? shakeTimesLimit : shakeTimes;
        CfgDailyShake.PrizePool prizePool = prizePools.stream().filter(tmp -> tmp.getShakeTimes() == finalShakeTimes).findFirst().orElse(null);
        List<Award> awards = prizePool.getAwards();
        List<Integer> prizePloolProbs = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(prizePloolProbs);
        return awards.get(awardIndex);
    }

    /**
     * 获得第三个奖池的奖励
     *
     * @param shakeTimes
     * @return
     */
    public static Award getThirdPrizePoolWelfare(int shakeTimes) {
        List<CfgDailyShake.PrizePool> prizePools = getCfgDailyShake().getThirdPrizePools();
        int shakeTimesLimit = getShakeTimesLimit();
        final int finalShakeTimes = shakeTimes > shakeTimesLimit ? shakeTimesLimit : shakeTimes;
        CfgDailyShake.PrizePool prizePool = prizePools.stream().filter(tmp -> tmp.getShakeTimes() == finalShakeTimes).findFirst().orElse(null);
        List<Award> awards = prizePool.getAwards();
        List<Integer> prizePloolProbs = awards.stream().map(Award::getProbability).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(prizePloolProbs);
        return awards.get(awardIndex);
    }

    /**
     * 获得本次每日摇一摇奖励
     *
     * @param shakeTimes
     * @return
     */
    public static List<Award> getDailyShakeAwards(int shakeTimes) {
        List<Award> awards = new ArrayList<>();
        awards.add(getFirstPrizePoolAwards(shakeTimes));
        awards.add(getSecondPrizePoolAwards(shakeTimes));
        awards.add(getThirdPrizePoolWelfare(shakeTimes));
        return awards;
    }

    /**
     * 获得本次每日摇一摇福利
     *
     * @param awardId
     * @return
     */
    public static CfgDailyShake.Welfare getWelfare(int awardId) {
        //获得福利效果集合
        List<CfgDailyShake.Welfare> welfares = getCfgDailyShake().getWelfares();
        welfares = welfares.stream().filter(tmp -> tmp.getType() == awardId).collect(Collectors.toList());
        //获得福利触发概率集合
        List<Integer> welfaresProbs = welfares.stream().map(CfgDailyShake.Welfare::getProb).collect(Collectors.toList());
        Integer welfareIndex = PowerRandom.hitProbabilityIndex(welfaresProbs);
        //返回本次摇一摇福利
        return welfares.get(welfareIndex);
    }

    /**
     * 获得福利类别
     *
     * @return
     */
    public static List<Integer> getWelfareType() {
        return getCfgDailyShake().getWelfares().stream().map(CfgDailyShake.Welfare::getType).collect(Collectors.toList());
    }
}
