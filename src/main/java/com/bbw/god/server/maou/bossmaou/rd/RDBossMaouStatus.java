package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suchaobin
 * @description 魔王状态信息
 * @date 2020/7/24 12:19
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RDBossMaouStatus extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7671300444237240179L;
    private Long remainTime = null;// 魔王过多久
    private Integer nextOpenTime = null;// 下轮开启时间
    private Map<String, List<Integer>> attackMaouCards = new HashMap<>();// 魔王编组卡牌

    public RDBossMaouStatus(Integer nextOpenTime, Map<String, List<Integer>> attackMaouCards) {
        this.nextOpenTime = nextOpenTime;
        this.attackMaouCards = attackMaouCards;
    }

    public RDBossMaouStatus(Long remainTime, Map<String, List<Integer>> attackMaouCards) {
        this.remainTime = remainTime;
        this.attackMaouCards = attackMaouCards;
    }
}
