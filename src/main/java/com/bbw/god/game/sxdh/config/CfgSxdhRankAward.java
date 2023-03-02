package com.bbw.god.game.sxdh.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgSxdhRankAward implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private List<CfgSxdhRankAwardEntity> phaseAwards;// 日排行奖励
    private List<CfgSxdhRankAwardEntity> middleSeasonAwards;//季中奖励
    private List<CfgSxdhRankAwardEntity> seasonAwards;//赛季奖励

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
