package com.bbw.god.game.combat.data;

import com.bbw.common.PowerRandom;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 战斗护符
 *
 * @author: suhq
 * @date: 2021/11/12 1:59 下午
 */
@Getter
@Setter
public class CombatBuff implements Serializable {
    private static final long serialVersionUID = 6945598528279824625L;
    private int runeId;
    private int level;
    /** 额外加成 攻击符图卡槽1效果加成50% 即extraRate为50 */
    private int extraRate;
    /** 持续的回合数 */
    private Integer round = 999;
    /** 是否发动 */
    private boolean isToPerform = true;
    /** 最近一次发动时的回合数（当前仅有用到） **/
    private int lastPerformRound = -1;

    /** 已触发次数(当前只有诛仙阵：长生词条用到) */
    private int triggeredNum = 0;

    public CombatBuff() {
    }

    public CombatBuff(int runeId, int level) {
        this.runeId = runeId;
        this.level = level;
    }

    public CombatBuff(int runeId, int level, int extraRate) {
        this.runeId = runeId;
        this.level = level;
        this.extraRate = extraRate;
    }

    /**
     * 是否可发动buff,每次调用都重新随机判定
     *
     * @param baseProb      基础概率
     * @param levelProbStep 等级概率步进
     * @return
     */
    public boolean ifToPerform(int baseProb, int levelProbStep) {
        int prob = baseProb + levelProbStep * level;
        isToPerform = PowerRandom.hitProbability(prob);
        return isToPerform;
    }

    /**
     * 是否已经失效
     *
     * @return
     */
    public boolean ifInvalid() {
        return !isToPerform || round <= 0;
    }

    /**
     * 扣除持续的的回合数。回合结束后调用。
     */
    public void deductRound() {
        if (round == 0) {
            return;
        }
        round--;
    }
}