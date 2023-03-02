package com.bbw.god.game.sxdh.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙大会排行
 *
 * @author suhq
 * @date 2020-04-23 14:44
 **/
@Data
public class CfgSxdhRankAwards implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private List<CfgSxdhRankAwardEntity> dayAwards;// 每日奖励
    private List<CfgSxdhRankAwardEntity> middleSeasonAwards;// 季中奖励
    private List<CfgSxdhRankAwardEntity> seasonAwards;// 赛季奖励

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}
