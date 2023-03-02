package com.bbw.god.server.maou.bossmaou.attackinfo;

import lombok.Data;

import java.util.Date;

/**
 * @author suhq
 * @description: 魔王回合数据
 * @date 2019-12-23 16:24
 **/
@Data
public class BossMaouRoundDetail {
    private Long maouId;
    private Integer round;
    private Integer type;
    private Date roundBegin;
    private Date roundEnd;

    public boolean ifMatch(long now) {
        return now >= this.roundBegin.getTime() && now < this.roundEnd.getTime();
    }
}
