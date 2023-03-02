package com.bbw.god.activity.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.cfg.CfgBrocadeGiftConfig;
import com.bbw.god.activity.config.BrocadeGiftTool;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 锦礼返回参数
 *
 * @author fzj
 * @date 2022/2/9 16:23
 */
@Data
public class RDBrocadeGift extends RDSuccess {
    /** 活动剩余时间 */
    private Long activityRemainTime;
    /** 当前投注 */
    private List<RDCurrentBetInfo> currentBetInfos;
    /** 上一轮投注 */
    private List<RDLasTurnBetInfo> lasTurnBetInfos;


    @Data
    public static class RDCurrentBetInfo {
        /** id */
        private Integer id;
        /** 类型 */
        private Integer type;
        /** 当前投注号码 */
        private List<String> betNums;
        /** 全服总投注 */
        private Integer gameTotalBetTimes;
        /** 剩余开奖时间 */
        private Long remainTime;
        /** 奖品 */
        private List<RDAward> awards;
        /** 投注需要消耗法宝 */
        private Integer betNeedTreasure;
        /** 需要数量 */
        private Integer needNum;
        /** 当前轮数 */
        private Integer currentTurn;
        /** 总轮数 */
        private Integer totalTurn;

        public static RDCurrentBetInfo getInstance(CfgBrocadeGiftConfig.TurnAndAwards turnAndAward) {
            RDCurrentBetInfo rd = new RDCurrentBetInfo();
            rd.setId(turnAndAward.getId());
            rd.setType(turnAndAward.getType());
            Date end = DateUtil.fromDateTimeString(turnAndAward.getEnd());
            rd.setRemainTime(DateUtil.millisecondsInterval(DateUtil.addMinutes(end, 1), DateUtil.now()));
            rd.setAwards(RDAward.getInstances(turnAndAward.getAwards()));
            rd.setBetNeedTreasure(turnAndAward.getDrawNeedTreasure());
            rd.setNeedNum(turnAndAward.getNum());
            rd.setCurrentTurn(turnAndAward.getTurn());
            //获取总共轮数
            Integer totalTurn = BrocadeGiftTool.getTotalTurn();
            rd.setTotalTurn(totalTurn);
            return rd;
        }
    }

    @Data
    public static class RDLasTurnBetInfo {
        /** id */
        private Integer id;
        /** 类型 */
        private Integer type;
        /** 当前投注号码 */
        private List<String> betNums;
        /** 奖品 */
        private List<RDAward> awards;
        /** 中奖号码 */
        private String lotteryNum;
        /** 中奖玩家 */
        private String lotteryPlayer;
        /** 全服总投注 */
        private Integer gameTotalBetTimes;

        public static RDLasTurnBetInfo getInstance(CfgBrocadeGiftConfig.TurnAndAwards turnAndAward) {
            RDLasTurnBetInfo rd = new RDLasTurnBetInfo();
            rd.setId(turnAndAward.getId());
            rd.setType(turnAndAward.getType());
            rd.setAwards(RDAward.getInstances(turnAndAward.getAwards()));
            return rd;
        }
    }
}
