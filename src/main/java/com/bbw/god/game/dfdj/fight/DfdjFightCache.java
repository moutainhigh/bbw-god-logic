package com.bbw.god.game.dfdj.fight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author suchaobin
 * @description 巅峰对决战斗缓存
 * @date 2021/1/26 11:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DfdjFightCache {
    private Long p1;
    private Integer score1;
    private Integer rank1;
    private Long p2;
    private Integer score2;
    private Integer rank2;

    public int getScore(long uid) {
        if (uid == p1) {
            return score1;
        }
        if (uid == p2) {
            return score2;
        }
        return 0;
    }

    public int getRank(long uid) {
        if (uid == p1) {
            return rank1;
        }
        if (uid == p2) {
            return rank2;
        }
        return 0;
    }
}
