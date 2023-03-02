package com.bbw.god.gameuser.leadercard;

import lombok.Data;

@Data
public class CacheLeaderCard {
    /**
     * 随机选择技能的次数
     */
    private Integer randomTimes=0;
    /**
     * 最后一次随机的技能
     */
    private Integer lastRandomSkill=0;
}
