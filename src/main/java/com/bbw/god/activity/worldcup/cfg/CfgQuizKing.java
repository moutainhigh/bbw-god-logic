package com.bbw.god.activity.worldcup.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 世界杯-绿茵活动-我是竞猜王
 * @author: hzf
 * @create: 2022-11-11 14:00
 **/
@Data
public class CfgQuizKing implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    /** 竞猜内容 */
    private List<CfgBet> bets;
    /** 竞猜 奖励Id */
    private Integer betAwardId;
    /** 竞猜奖励 */
    private List<CfgBetAwardRule> betAwardRules;

    @Data
    public static class CfgBet{
        private String dayDate;
        /** id:比赛标识 */
        private String id;
        /** 1：是小组赛 ，0：不是小组赛 */
        private Integer groupStage;
        /** 参与国家 */
        private List<Integer> competeCountries;
        /** 赢的国家 */
        private Integer winCountry;
        /** 竞猜开始时间 */
        private String betBegin;
        /**  竞猜结束时间 */
        private String betEnd;
        /** 竞猜消耗的道具 */
        private Integer needTreasure;
        /** 竞猜消耗道具的数量 */
        private Integer num;
    }
    @Data
    public static class CfgBetAwardRule{
        /** true:中 ， false:不中 */
        private Boolean success;
        /** 奖励  */
        private int num;
    }
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
