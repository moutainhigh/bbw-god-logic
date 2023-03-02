package com.bbw.god.game.maou.ctrl;

import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 跨服魔王信息
 *
 * @author: suhq
 * @date: 2021/12/17 10:43 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGameMaouAttack extends RDCommon implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    /** 剩余血量 */
    private Integer remainBlood;
    /** 玩家剩余攻击次数 */
    private Integer remainAttackTimes;
    /** 当前目标 */
    private Integer curTarget;
    /** 目标状态 */
    private Integer curTargetStatus;
    /** 打掉的魔王血量 */
    private Integer beatedBlood;
    /** 打掉的总的魔王血量 */
    private Integer totalBeatedBlood;
    /** 开始剩余时间 */
    private Long remainTimeToBegin = 0L;

    public static RDGameMaouAttack getInstance(int beatedBlood, int remainBlood, GameMaouAttacker attacker) {
        RDGameMaouAttack rd = new RDGameMaouAttack();
        rd.setBeatedBlood(beatedBlood);
        rd.setRemainBlood(remainBlood);
        rd.setRemainAttackTimes(attacker.getRemainAttackTimes());
        GameMaouAttacker.Target target = attacker.gainCurTarget();
        rd.setCurTarget(target.getTargetId());
        rd.setCurTargetStatus(target.getStatus());
        rd.setTotalBeatedBlood(attacker.getAttackBlood());
        return rd;
    }
}
