package com.bbw.god.game.zxz.cfg;

import com.bbw.god.game.config.card.FightCardGenerateRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 诛仙阵野怪基本配置信息
 * @author: hzf
 * @create: 2022-09-14 16:08
 **/
@Data
public class CfgZxzDefenderCardRule implements  Serializable {
    private static final long serialVersionUID = 1L;
    /** 难度类型 */
    private Integer difficulty;
    /** 关卡*/
    private Integer defender;
    /** 种类 */
    private Integer kind;
    /** 召唤师等级 */
    private Integer summonerLv;
    /** 召唤师血量 */
    private Integer bloodBarNum;
    /** 卡牌等级 */
    private Integer cardLv;
    /** 卡牌阶数 */
    private Integer cardHv;
    /** 技能随机 */
    private Integer skillRandom;
    /** 符图随机 */
    private List<CfgFutTu> fuTus;
    /**卡牌数据 */
    private List<FightCardGenerateRule> cards;

    @Data
    public static class CfgFutTu implements  Serializable { //问题 ？？？？
        private static final long serialVersionUID = 1L;
        /** 符图类型 */
        private List<Integer> fuTuTypes;
        /** 符图的品质 */
        private List<Integer> fuTuQualitys;
//        private List<Integer> fuTuHvs;

    }

}
