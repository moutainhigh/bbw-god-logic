package com.bbw.god.activity.worldcup.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 世界杯-绿茵活动-超级16强配置
 * @author: hzf
 * @create: 2022-11-11 12:43
 **/
@Data
public class CfgSuper16 implements CfgEntityInterface, Serializable {
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
    /** 竞猜奖励 */
    private List<CfgQuizAward> quizAwards;


    @Data
    public static class CfgQuiz {
        private String group;
        private List<Integer> competeCountries;
        private List<Integer> winCountry;
        private Integer needTreasure;
        private Integer num;
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
