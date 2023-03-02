package com.bbw.god.server.fst.game;

import com.bbw.common.DateUtil;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.server.fst.FstVideoLog;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明：跨服封神台信息
 *
 * @author lwb
 * date 2021-06-30
 */
@Data
public class FstGameRanking extends GameData {
    private Integer rankingType=FstRankingType.NONE.getType();
    /**上一次结算榜单类型  */
    private Integer preRankingType=FstRankingType.NONE.getType();
    private Integer preRank=-1;
    /** 今日战斗次数 */
    private Integer todayFightTimes=0;
    /** 最近一次重置时间*/
    private Integer lastUpdateDate= DateUtil.getTodayInt();
    /** 最近一次跨服封神台挑战时间*/
    private Date lastChallengeDate= DateUtil.now();
    /** 保存最近50条记录 */
    private List<FstVideoLog> videoLogs =new ArrayList<>();
    private Integer showPop=0;
    private Date showSettleTip=DateUtil.now();
    
    public void deductChallengeNum() {
        this.todayFightTimes++;
    }
    
    public static FstGameRanking getInstance(long uid){
        FstGameRanking fstGameRanking=new FstGameRanking();
        fstGameRanking.setId(uid);
        return fstGameRanking;
    }
    public void resetToDayData(){
        todayFightTimes=0;
        lastUpdateDate=DateUtil.getTodayInt();
    }
    
    public void addChallengeNum() {
        this.todayFightTimes--;
    }
    @Override
    public GameDataType gainDataType() {
        return GameDataType.GAME_FST;
    }
}
