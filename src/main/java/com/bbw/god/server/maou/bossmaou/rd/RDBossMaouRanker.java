package com.bbw.god.server.maou.bossmaou.rd;

import com.bbw.common.ListUtil;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 魔王排行信息
 * @date 2019-12-23 11:10
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDBossMaouRanker {
    private static final long serialVersionUID = 1L;
    private Integer beatedBlood = null;// 打掉的血量
    private Integer beatedNum = null;// 攻击次数
    private String nickName = null;
    private Integer level = null;
    private Integer head = null;

    public static RDBossMaouRanker getInstance(BossMaouAttackSummary mr) {
        RDBossMaouRanker rdRanker = new RDBossMaouRanker();
        rdRanker.setNickName(mr.getNickname());
        rdRanker.setLevel(mr.getLevel());
        rdRanker.setHead(mr.getHead());
        rdRanker.setBeatedBlood(mr.getBeatedBlood());
        rdRanker.setBeatedNum(mr.getAttackTimes());
        return rdRanker;
    }

    public static List<RDBossMaouRanker> getInstances(List<BossMaouAttackSummary> rankers) {
        if (ListUtil.isEmpty(rankers)) {
            return new ArrayList<>();
        }
        List<RDBossMaouRanker> instances = rankers.stream().map(tmp -> RDBossMaouRanker.getInstance(tmp)).collect(Collectors.toList());
        return instances;
    }
}
