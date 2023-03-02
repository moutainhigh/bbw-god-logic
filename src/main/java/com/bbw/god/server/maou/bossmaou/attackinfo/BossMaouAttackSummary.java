package com.bbw.god.server.maou.bossmaou.attackinfo;

import lombok.Data;

/**
 * @author suhq
 * @description: 魔王攻击信息
 * @date 2019-12-23 14:53
 **/
@Data
public class BossMaouAttackSummary {
    private Long maouId;// 魔王ID
    private Long guId;// 玩家ID
    private String nickname;// 昵称
    private Integer head;// 头像
    private Integer level;// 等级
    private Integer beatedBlood = 0;// 打掉的血量
    private Integer lastAttackRound = 0;//上一次攻打回合
    private Long lastAttackTime;// 上一次攻击时间,时间的毫秒数
    private Integer attackTimes = 0;// 攻击次数
    private Long awardMailId = -1L;//奖励邮件的ID

    public void incBeatedBlood(int add) {
        this.beatedBlood += add;
    }

    public void incAttackTimes(int add) {
        this.attackTimes += add;
    }
}
