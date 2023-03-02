package com.bbw.god.activity.worldcup.rd;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.worldcup.cfg.CfgQuizKing;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每天竞猜王返回类
 * @author: hzf
 * @create: 2022-11-14 19:19
 **/
@Data
public class RdDayDateQuizKing extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    private List<RdDateQuizKing> dayDateQuizKings;
    private Integer status;

    public static RdDayDateQuizKing instance(UserQuizKingInfo quizKingInfo,List<CfgQuizKing.CfgBet> cfgBets){
        RdDayDateQuizKing rdDayDateQuizKing = new RdDayDateQuizKing();
        rdDayDateQuizKing.setDayDateQuizKings(RdDateQuizKing.getRdDateQuizKing(quizKingInfo,cfgBets));
        rdDayDateQuizKing.setStatus(ListUtil.isEmpty(cfgBets)?0:1);
        return rdDayDateQuizKing;
    }

    @Data
    public static class RdDateQuizKing{
        private String id;

        private List<Integer> competeCountries;

        /** 1：是小组赛 ，0：不是小组赛 */
        private Integer groupStage;


        /** 玩家投注记录 */
        private List<RdBetCountry> betCountry;
        private Integer winCountry;
        private String betBegin;
        private long surplusTime;

        public static List<RdDateQuizKing> getRdDateQuizKing(UserQuizKingInfo quizKingInfo,List<CfgQuizKing.CfgBet> cfgBets){
            List<RdDateQuizKing> rdDateQuizKings = new ArrayList<>();
            for (CfgQuizKing.CfgBet cfgBet : cfgBets) {
                RdDateQuizKing rdDateQuizKing = new RdDateQuizKing();
                rdDateQuizKing.setId(cfgBet.getId());
                rdDateQuizKing.setGroupStage(cfgBet.getGroupStage());

                //竞猜结束转化
                Date betEndDate = DateUtil.fromDateTimeString(cfgBet.getBetEnd());
                //比赛开始时间 是 竞猜结束时间加一个小时
                String toDateTimeString = DateUtil.toDateTimeString(DateUtil.addHours(betEndDate, 1));
                rdDateQuizKing.setBetBegin(toDateTimeString);
                // 计算比赛剩余时间
                rdDateQuizKing.setSurplusTime(betEndDate.getTime() - System.currentTimeMillis());

                rdDateQuizKing.setCompeteCountries(cfgBet.getCompeteCountries());
                rdDateQuizKing.setWinCountry(cfgBet.getWinCountry());
                List<RdBetCountry> rdBetCountry = new ArrayList<>();
                if (null != quizKingInfo) {
                    rdBetCountry = RdBetCountry.getRdBetCountry(quizKingInfo, cfgBet.getId());
                }
                rdDateQuizKing.setBetCountry(rdBetCountry);
                rdDateQuizKings.add(rdDateQuizKing);
            }
            return rdDateQuizKings;

        }
    }

    @Data
    public static class RdBetCountry {
        private Integer betCountry;
        private Integer betNum;

        public static List<RdBetCountry> getRdBetCountry(UserQuizKingInfo quizKingInfo, String id){
            List<UserQuizKingInfo.BetRecord> betRecords = quizKingInfo.gainBetRecords(id);
            System.out.println("betRecords = " + betRecords);
            List<RdBetCountry> rdBetCountries = new ArrayList<>();
            for (UserQuizKingInfo.BetRecord betRecord : betRecords) {
                RdBetCountry rdBetCountry = new RdBetCountry();
                rdBetCountry.setBetCountry(betRecord.getBetCountry());
                rdBetCountry.setBetNum(betRecord.getBetNum());
                rdBetCountries.add(rdBetCountry);
            }
            return rdBetCountries;
        }
    }
}
