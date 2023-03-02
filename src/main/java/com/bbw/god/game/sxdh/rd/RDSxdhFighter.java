package com.bbw.god.game.sxdh.rd;

import com.bbw.god.login.RDGameUser.RDTreasure;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家登陆强联网返回的数据
 *
 * @author suhq
 * @date 2019-06-21 09:59:52
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSxdhFighter extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer ticket = 0;
    private Integer freeTimes = 0;
    private Integer bean = 0;
    private Integer winTimes = 0;//胜利次数
    private Integer maxStreak = 0;//最大连胜
    private Integer joinTimes = 0;//参战次数
    private Integer winRate = 0;//胜率
    //	private Integer isAwardedBean = 1;// 是否已领取仙豆
    // 积分
    private Integer score = 0;
    private Integer todayScore = 0;
    // 排名
    private Integer rank = 0;
    private Integer rankTrend = 0;// 排名趋势
    private Integer todayRank = 0;
    private Integer todayRankTrend = 0;

    private Integer phase;//阶段(10107,10814,11521,12228,12929,13030,13131)
    private String phaseDes;// 阶段描述（eg:22-28日积分/今日积分）
    private Integer phaseScore;//阶段积分
    private Integer phaseRank;//阶段排行
    private Integer isDoubleScore;// 是否双倍积分
    private Integer dailySprintAwardStatus;//冲刺每日福利状态
    /** 特殊赛季剩余挑战次数 */
    private Integer remainMatchTimes;

    // 称号
//    private Integer title = 1;
//    private Integer nextTitle = 0;
//    private Integer nextTitleNeedScore = 0;
    // 丹药
    private List<RDTreasure> medicine = null;
}
