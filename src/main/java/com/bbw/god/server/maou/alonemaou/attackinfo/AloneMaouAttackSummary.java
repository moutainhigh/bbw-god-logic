package com.bbw.god.server.maou.alonemaou.attackinfo;

import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.server.maou.alonemaou.AloneMaouTool;
import lombok.Data;

/**
 * @author suhq
 * @description: 玩家独占魔王攻打记录
 * @date 2019-12-17 16:30
 **/
@Data
public class AloneMaouAttackSummary {
    private Long maouId;
    private Long guId;
    private Integer remainResetTimes;//剩余重置次数
    private Integer freeAttackTimes;//免费挑战次数
    private Integer attackTimes;//攻击次数
    private Integer boughtTimes;//已购买次数
    private Integer attackingMaouLevel;//正在攻击的魔王级别
    private Integer accBlood;//累计打掉的血量
    private Long nextAttackTime;//下一次挑战时间

    public static AloneMaouAttackSummary getInstance(long uid, long maouId) {
        CfgAloneMaou aloneMaouConfig = AloneMaouTool.getConfig();
        AloneMaouAttackSummary obj = new AloneMaouAttackSummary();
        obj.setMaouId(maouId);
        obj.setGuId(uid);
        obj.setRemainResetTimes(aloneMaouConfig.getResetTimesLimit());
        obj.setFreeAttackTimes(aloneMaouConfig.getFreeAttackTimes());
        obj.setAttackTimes(0);
        obj.setBoughtTimes(0);
        obj.setAttackingMaouLevel(1);
        obj.setAccBlood(0);
        obj.setNextAttackTime(System.currentTimeMillis());
        return obj;
    }

    public void updateAfterAttack(boolean isUseGold, int beatedBlood, int attackingMaouLevel) {
        if (isUseGold) {
            this.boughtTimes++;
        } else {
            this.freeAttackTimes--;
        }
        this.attackTimes++;
        this.accBlood += beatedBlood;
        this.nextAttackTime = System.currentTimeMillis() + 3 * 1000;
        this.attackingMaouLevel = attackingMaouLevel;
    }

    public void reset(boolean isAutoReset) {
        CfgAloneMaou config = AloneMaouTool.getConfig();
        this.freeAttackTimes = config.getFreeAttackTimes();
        if (isAutoReset) {
            this.remainResetTimes = config.getResetTimesLimit();
        } else {
            //扣除重置次数
            this.remainResetTimes--;
        }

        this.attackTimes = 0;
        this.boughtTimes = 0;
        this.attackingMaouLevel = 1;
        this.accBlood = 0;
    }

    public void addFreeTime() {
        this.freeAttackTimes++;
    }

    public void setNextMaouLevel(int nextMaouLevel) {
        if (!ifTopMaouLevel()) {
            this.attackingMaouLevel = nextMaouLevel;
        }
    }

    public boolean ifTopMaouLevel() {
        return this.attackingMaouLevel >= 10;
    }
}
