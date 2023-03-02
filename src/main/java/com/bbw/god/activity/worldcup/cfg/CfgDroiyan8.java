package com.bbw.god.activity.worldcup.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 世界杯-绿茵活动-决战8强
 * @author: hzf
 * @create: 2022-11-11 13:38
 **/
@Data
public class CfgDroiyan8 implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 展示开始时间 */
    private String showBegin;
    /** 展示结束时间 */
    private String showEnd;

    /** 竞猜内容 */
    private List<CfgQuiz> quizs;
    /** 竞猜连中规则 */
    private List<CfgContinuousSuccess> continuousSuccess;
    /** 对应的奖励 */
    private Integer betAwardId;


    @Data
    public static class CfgQuiz{
        /** id:比赛标识 */
        private String id;
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
    public static class CfgContinuousSuccess{
        /** 连中次数 */
        private Integer continuousSuccessTimes;
        /** 数量 */
        private Integer num;
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
