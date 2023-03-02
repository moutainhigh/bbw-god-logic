package com.bbw.god.game.transmigration.cfg;

import com.bbw.god.game.config.card.FightCardGenerateRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回防守者
 *
 * @author: suhq
 * @date: 2021/9/10 10:25 上午
 */
@Data
public class CfgTransmigrationDefender implements Serializable {
    private static final long serialVersionUID = 3095926888734005517L;
    private Integer cityLv;
    /** 防守者属性，0表示全属性,其他[10,20,30,40,50] */
    private Integer type;
    private Integer defenderLv;
    private List<Integer> defenderRunes;
    private Integer runeNum;
    private Integer[] cardHvInterval;
    private Integer[] cardLvInterval;
    private List<FightCardGenerateRule> cards;

}
