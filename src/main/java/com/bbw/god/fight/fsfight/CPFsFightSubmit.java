package com.bbw.god.fight.fsfight;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家竞技战斗提交
 *
 * @author suhq
 * @date 2019-06-27 15:14:24
 */
@Data
public class CPFsFightSubmit implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long combatId = 0L;
    private Integer roomId;
    private Long winner;
    private Integer winnerOnline;
    private String winnerNickname;
    private Long loser;
    private Integer loserOnline;
    private String loserNickname;
    private String extra;// json
}
