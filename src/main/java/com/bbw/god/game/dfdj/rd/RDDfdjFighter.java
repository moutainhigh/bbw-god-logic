package com.bbw.god.game.dfdj.rd;

import com.bbw.god.login.RDGameUser;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家登陆强联网返回的数据
 * @date 2021/1/5 13:56
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjFighter extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer bean = 0;
    private Integer winTimes = 0;//胜利次数
    private Integer maxStreak = 0;//最大连胜
    private Integer joinTimes = 0;//参战次数
    private Integer winRate = 0;//胜率
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

    private Boolean isOpen;// 当前是否开启
    // 丹药
    private List<RDGameUser.RDTreasure> medicine = null;
}
