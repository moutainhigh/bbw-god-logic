package com.bbw.god.game.maou.ctrl;

import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 跨服魔王信息
 *
 * @author: suhq
 * @date: 2021/12/17 10:43 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGameMaouInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    /** 编组卡牌 */
    private List<Integer> attackMaouCards = new ArrayList<>();
    /** 魔王总血量 */
    private Integer totalBlood;
    /** 剩余血量 */
    private Integer remainBlood;
    /** 打掉的总的魔王血量 */
    private Integer totalBeatedBlood;
    /** 玩家剩余攻击次数 */
    private Integer remainAttackTimes;
    /** 当前目标 */
    private Integer curTarget;
    /** 魔王类别 */
    private Integer maouType;
    /** 魔王ID */
    private Integer maouId;
    /** 目标状态 */
    private Integer curTargetStatus;
    /** 当前轮次 */
    private Integer curTurn;
    /** 开始剩余时间 */
    private Long remainTimeToBegin = 0L;

    public static RDGameMaouInfo getInstance(int totalBlood, int remainBlood, GameMaouAttacker attacker) {
        RDGameMaouInfo rd = new RDGameMaouInfo();
        rd.setTotalBlood(totalBlood);
        rd.setRemainBlood(remainBlood);
        rd.setAttackMaouCards(attacker.getCards());
        rd.setRemainAttackTimes(attacker.getRemainAttackTimes());
        GameMaouAttacker.Target target = attacker.gainCurTarget();
        rd.setCurTarget(target.getTargetId());
        rd.setCurTargetStatus(target.getStatus());
        rd.setTotalBeatedBlood(attacker.getAttackBlood());
        return rd;
    }
}
