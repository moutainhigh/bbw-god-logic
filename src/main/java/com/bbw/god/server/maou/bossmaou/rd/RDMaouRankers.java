package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 魔王排行信息
 *
 * @author suhq
 * @date 2019年12月24日 下午7:07:06
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMaouRankers extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -6690434170449261267L;
    private Integer myBeatedBlood = 0;// 我打掉的血量
    private Integer myBeatedTimes = 0;// 我的攻打次数
    private Integer myRanking = 0;// 我的排行
    private List<RDBossMaouRanker> rankers;//排行信息

    public static RDMaouRankers getInstance(List<BossMaouAttackSummary> rankers) {
        RDMaouRankers rd = new RDMaouRankers();
        rd.setRankers(RDBossMaouRanker.getInstances(rankers));

        return rd;
    }
}
