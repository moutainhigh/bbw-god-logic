package com.bbw.god.server.maou.alonemaou.attackinfo;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.server.maou.ServerMaouStatus;
import com.bbw.god.server.maou.alonemaou.AloneMaouTool;
import com.bbw.god.server.maou.alonemaou.maouskill.MaouSkillEnum;
import lombok.Data;

/**
 * @author suhq
 * @description: 独占魔王层级信息
 * @date 2019-12-17 18:33
 **/
@Data
public class AloneMaouLevelInfo {
    private Long maouId;
    private Long guId;
    private Integer maouLevel;//魔王级别
    private Integer remainBlood;//剩余血量
    private Integer totalBlood;//总血量
    private Integer remainShield;//剩余护盾
    private Integer totalShield;//总护盾
    private Integer shildDefence;//护盾防御
    private Integer maouSkill;//魔王技能；
    private Integer round = 0;//攻打回合数
    private Integer maxRound = CfgAloneMaou.UNLIMIT_ROUND;//最大回合数

    public static AloneMaouLevelInfo getInstance(long uid, long maouId, CfgAloneMaou.AloneMaou aloneMaou) {
        AloneMaouLevelInfo obj = new AloneMaouLevelInfo();
        obj.setMaouId(maouId);
        obj.setGuId(uid);
        obj.setMaouLevel(aloneMaou.getLevel());

        obj.setTotalBlood(aloneMaou.getBlood());
        obj.setRemainBlood(obj.getTotalBlood());

        obj.setTotalShield(0);
        obj.setRemainShield(0);

        obj.setShildDefence(aloneMaou.getShieldDefence());
        obj.setMaouSkill(aloneMaou.getMauSkills().get(0));
        obj.setMaxRound(aloneMaou.getLimitRound());

        return obj;
    }

    public void updateMaouSkill() {
        if (this.maouLevel == 10) {
            int lastSkill = this.maouSkill;
            CfgAloneMaou.AloneMaou aloneMaou = AloneMaouTool.getMaouConfig(10);
            int skill = 0;
            do {
                skill = PowerRandom.getRandomFromList(aloneMaou.getMauSkills());
            } while (lastSkill == skill);
            MaouSkillEnum maouSkillEnum = MaouSkillEnum.fromValue(skill);
            if (maouSkillEnum == MaouSkillEnum.SHIELD) {
                this.setTotalShield(aloneMaou.getShield());
                this.setRemainShield(this.getTotalShield());
            } else {
                this.setTotalShield(0);
                this.setRemainShield(0);
            }
//            System.out.println("魔王新技能：" + skill);
            this.setMaouSkill(skill);
        }
    }

    /**
     * 是否回合限制
     *
     * @return
     */
    public boolean ifRoundLimit() {
        return this.maxRound < CfgAloneMaou.UNLIMIT_ROUND;
    }

    /**
     * 魔王已死
     *
     * @return
     */
    public boolean isKilled() {
        return this.remainBlood < 1;
    }

    public boolean ifLeave() {
        if (ifRoundLimit() && this.maxRound - this.round <= 0) {
            return true;
        }
        return false;
    }

    public void addRound() {
        this.round++;
    }

    public void addBlood(int value) {
        this.remainBlood += value;
    }

    public void lostBlood(int value) {
        this.remainBlood = this.remainBlood - value;
        this.remainBlood = Math.max(this.remainBlood, 0);
    }

    public void lostShield(int value) {
        this.remainShield = this.remainShield - value;
        this.remainShield = Math.max(this.remainShield, 0);
    }

    public void reset() {
        this.round = 0;
        this.remainBlood = this.totalBlood;
        this.remainShield = 0;
        this.totalShield = 0;
        CfgAloneMaou.AloneMaou aloneMaou = AloneMaouTool.getMaouConfig(this.maouLevel);
//        System.out.println(aloneMaou.getMauSkills());
        this.maouSkill = aloneMaou.getMauSkills().get(0);
    }

    /**
     * 获得魔王状态
     *
     * @return
     */
    public ServerMaouStatus gainMaouStatus() {
        if (this.remainBlood <= 0) {
            return ServerMaouStatus.KILLED;
        }
        if (ifRoundLimit() && this.maxRound - this.round <= 0) {
            return ServerMaouStatus.LEAVE;
        }
        return ServerMaouStatus.ATTACKING;
    }
}
