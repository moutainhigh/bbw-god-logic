package com.bbw.god.game.maou.ctrl;

import com.bbw.god.game.maou.GameMaouAttacker;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 跨服魔王奖励领取
 *
 * @author: suhq
 * @date: 2021/12/21 5:49 下午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGameMaouAward extends RDCommon implements Serializable {
    private static final long serialVersionUID = 7409189386250719040L;
    /** 当前目标 */
    private Integer curTarget;
    /** 目标状态 */
    private Integer curTargetStatus;
    /** 打掉的总的魔王血量 */
    private Integer totalBeatedBlood;

    public static RDGameMaouAward getInstance(GameMaouAttacker attacker) {
        RDGameMaouAward rd = new RDGameMaouAward();
        GameMaouAttacker.Target target = attacker.gainCurTarget();
        rd.setCurTarget(target.getTargetId());
        rd.setCurTargetStatus(target.getStatus());
        rd.setTotalBeatedBlood(attacker.getAttackBlood());
        return rd;
    }
}
