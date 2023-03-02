package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 魔王排行奖励信息
 *
 * @author suhq
 * @date 2019年12月24日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMaouRankerAwards extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -6690434170449261267L;
    Map<String, List<RDMaouRankerAward>> rankerAwards = new HashMap<>();//排行奖励信息

    public static RDMaouRankerAwards getInstance(List<CfgBossMaou.RankerAward> rankerAwards) {
        RDMaouRankerAwards rd = new RDMaouRankerAwards();
        Map<String, List<CfgBossMaou.RankerAward>> rankerAwardsMap =
                rankerAwards.stream().collect(Collectors.groupingBy(s -> s.getMaouLevel().toString()));
        for (String maouId : rankerAwardsMap.keySet()) {
            List<RDMaouRankerAward> rdRankerAwards = rankerAwardsMap.get(maouId).stream()
                    .map(RDMaouRankerAward::new).collect(Collectors.toList());
            RDMaouRankerAward first = rdRankerAwards.stream().filter(s ->
                    s.getMaxLevel() == 1).findFirst().orElse(null);
            List<RDAward> awards = first.getRankerAwards();
            awards.add(RDAward.getInstance(new Award(AwardEnum.KP, 1, 4)));
            rd.rankerAwards.put(maouId, rdRankerAwards);
        }
        return rd;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RDMaouRankerAward implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer maxLevel = null;// 最大排名
        private Integer minLevel = null;// 最小排名
        private List<RDAward> rankerAwards = null;// 奖励

        public RDMaouRankerAward(CfgBossMaou.RankerAward rankerAward) {
            this.maxLevel = rankerAward.getMinRank();
            this.minLevel = rankerAward.getMaxRank();
            this.rankerAwards = RDAward.getInstances(rankerAward.getAwards());
        }
    }
}
