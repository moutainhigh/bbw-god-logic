package com.bbw.god.game.dfdj.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgDfdjRankAward implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private List<CfgDfdjRankAwardEntity> phaseAwards;// 日排行奖励
    private List<CfgDfdjRankAwardEntity> middleSeasonAwards;//季中奖励
    private List<CfgDfdjRankAwardEntity> seasonAwards;//赛季奖励

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
