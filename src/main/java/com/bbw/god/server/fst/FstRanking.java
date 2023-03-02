package com.bbw.god.server.fst;

import com.bbw.common.DateUtil;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 封神台
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-26 14:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FstRanking extends ServerData implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long uid;// 玩家ID
//    private Integer ranking;// 排行
    private Integer points = 0;// 积分
    private Integer todayFightTimes=0;//今日发起战斗次数
    private Integer winTimes = 0;// 胜利次数
    private Integer winStreak = 0;// 连胜纪录
    private Integer incrementPoints = 0;
    private Integer challengeTotalTimes = 0;// 已经挑战的次数
    private Date lastChallengeTime;// 最近一次挑战的时间
    private Integer lastUpdateDate=null;//最后一次更新日期 yyyy-mm-dd
    private List<FstVideoLog> videoLogs=new ArrayList<>();

    public static FstRanking instance(long guId, int sId, int rank) {
        FstRanking fst = new FstRanking();
        fst.setId(guId);// 此处直接采用玩家ID作为封神台排名ID，修改会影响获取的实现
        fst.setUid(guId);
        fst.setSid(sId);
        fst.setLastChallengeTime(DateUtil.now());
        return fst;
    }

    /**
     * 重置 胜利次数，连胜，挑战次数
     */
    public void resetChallengeData() {
        this.winTimes = 0;
        this.winStreak = 0;
        this.challengeTotalTimes = 0;
        this.todayFightTimes = 0;
    }

    public void addChallengeNum() {
        this.todayFightTimes--;
    }

    public void deductChallengeNum() {
        this.todayFightTimes++;
    }

    public void addIncrementPoints(int num) {
        this.incrementPoints += num;
    }

    public void deductIncrementPoints(int num) {
        this.incrementPoints -= num;
    }

    public void addChallengeTotalNum() {
        this.challengeTotalTimes++;
    }

//	public void addPoint(int point) {
//		this.points += point;
//	}
//
//	public void deductPoint(int point) {
//		this.points -= point;
//	}

    /**
     * 封神台战斗失败数据操作
     */
    public void operateAsFail() {
        this.winStreak = 0;
    }

    /**
     * 封神台战斗胜利数据操作
     */
    public void operateAsWin() {
        this.winStreak++;
        this.winTimes++;
    }

    /**
     * 重置连胜
     */
    public void resetWinStreak() {
        this.winStreak = 0;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.FSTPVPRanking;
    }
}
