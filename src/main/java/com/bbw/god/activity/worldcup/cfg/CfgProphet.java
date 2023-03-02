package com.bbw.god.activity.worldcup.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 世界杯-绿茵活动-我是预言家
 * @author: hzf
 * @create: 2022-11-11 13:51
 **/
@Data
public class CfgProphet implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    /** 竞猜开始时间 */
    private String betBegin;
    /** 竞猜结束时间 */
    private String betEnd;
    /** 展示开始时间 */
    private String showBegin;
    /** 展示结束时间 */
    private String showEnd;
    /** 竞猜内容 */
    private List<CfgQuiz> quizs;
    /** 消耗的道具 */
    private Integer needTreasure;
    /** 消耗的数量 */
    private Integer needNum;
    /** 竞猜奖励 */
    private List<CfgQuizAward> quizAwards;

    @Data
    public static class CfgQuiz{
        /** id:比赛标识 */
        private String id;
        /** 参与国家 */
        private List<Integer> competeCountries;
        /** 赢的国家 */
        private Integer winCountry;

    }
    @Data
    public static class CfgQuizAward{
        /** 猜中次数 */
        private Integer  successTimes;
        /**奖励 */
        private List<Award> awards;
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
