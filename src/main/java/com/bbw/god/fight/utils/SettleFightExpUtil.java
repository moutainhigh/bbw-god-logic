package com.bbw.god.fight.utils;

import com.bbw.common.PowerRandom;
import com.bbw.god.fight.FightSubmitParam;

import java.util.List;

/**
 * 计算战斗经验的工具
 * @author：lwb
 * @date: 2020/12/21 14:38
 * @version: 1.0
 */
public class SettleFightExpUtil {

    /**
     *
     * 战斗奖励经验计算：
     * @param result
     * @return
     */
    public int settleFightExp(int oppLostBlood, List<FightSubmitParam.SubmitCardParam> killedCards) {
        int gainExp = 300 + oppLostBlood / 10;
        for (FightSubmitParam.SubmitCardParam cardParam:killedCards){
            //单卡计算公式：（星级经验*（1+卡牌等级）*（1+卡牌阶级*0.5））/10 =》取整
            gainExp += (int) (getStarAsExpFactor(cardParam.getStar()) * (1 + cardParam.getLv() * (1 +  cardParam.getHv() * 0.5) / 10.0));
        }
        // 最终数值需乘上0.9~1.1的随机修正值
        gainExp *= (PowerRandom.getRandomBySeed(21) + 89) / 100.0;
        return gainExp;
    }

    /**
     * 获得卡牌星级战斗经验因子
     *
     * @param star
     * @return
     */
    private static int getStarAsExpFactor(int star) {
        switch (star) {
            case 1:
                return 10;
            case 2:
                return 20;
            case 3:
                return 40;
            case 4:
                return 100;
            case 5:
                return 200;
            default:
                return 0;
        }
    }
}
