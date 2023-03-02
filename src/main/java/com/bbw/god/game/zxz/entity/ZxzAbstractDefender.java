package com.bbw.god.game.zxz.entity;

import lombok.Data;

import java.util.List;

/**
 * 诛仙阵 野怪抽象类
 * @author: hzf
 * @create: 2023-01-04 12:01
 **/
@Data
public abstract class ZxzAbstractDefender {
    /** 召唤师等级 */
    private Integer summonerLv;
    /** 卡组 卡牌ID@lv@hv@skill0,skill5,skill10 */
    private List<String> defenderCards;
    /** cardId@xianJueType@level@quality@starMapProgress@addition */
    private List<String> cardXianJues;
    /**cardId@zhiBaoId@property@addition@skillGroup */
    private List<String> cardZhiBaos;
    /** 符图数据 */
    private List<Integer> runes;
}
