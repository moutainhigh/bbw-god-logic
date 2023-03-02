package com.bbw.god.game.award;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 排名奖励
 *
 * @author suhq
 * @date 2020-04-27 13:35
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDRankerAward extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer maxRank = null;// 最大排名
    private Integer minRank = null;// 最小排名
    private List<RDAward> awards = null;

    public static RDRankerAward getInstance(RankerAward rankerAward) {
        RDRankerAward rd = new RDRankerAward();
        rd.setMinRank(rankerAward.getMinRank());
        rd.setMaxRank(rankerAward.getMaxRank());
        rd.setAwards(RDAward.getInstances(rankerAward.getAwards()));
        return rd;
    }

    public static List<RDRankerAward> getInstances(List<RankerAward> rankerAwards) {
        List<RDRankerAward> rdRankerAwards = new ArrayList<>();
        for (RankerAward rankerAward : rankerAwards) {
            rdRankerAwards.add(RDRankerAward.getInstance(rankerAward));
        }
        return rdRankerAwards;
    }
}
