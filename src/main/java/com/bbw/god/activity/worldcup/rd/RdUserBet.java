package com.bbw.god.activity.worldcup.rd;

import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 玩家投注记录
 * @author: hzf
 * @create: 2022-11-23 02:30
 **/
@Data
public class RdUserBet extends RDSuccess {

    /** 活动类型  */
    private int type;
    /** 总玩家数量 */
    private int totalNum;
    /** 超级16强投注记录 */
    private List<RdSuper16Bet> super16Bet;
    /** 决战8强 投注记录 */
    private List<RdDroiyan8Bet> droiyan8Bet;
    /** 我是预言家投注记录 */
    private List<RdProphetBet> prophetBet;
    /** 我是竞猜王投注记录 */
    private List<RdQuizKingBet> quizKingBet;
    /** 没有投注 用户id */
    private List<Long> notBetUids;

    @Data
    public static class RdSuper16Bet{
        private long gameUserId;
        /** 投注记录 分组 eg:A----> 投注记录 */
        private Map<String, UserSuper16Info.BetRecord> betRecords;

        public static RdSuper16Bet instance(UserSuper16Info userSuper16Info){
            RdSuper16Bet rdSuper16Bet = new RdSuper16Bet();
            rdSuper16Bet.setGameUserId(userSuper16Info.getGameUserId());
            rdSuper16Bet.setBetRecords(userSuper16Info.getBetRecords());
            return rdSuper16Bet;
        }
    }

    @Data
    public static class  RdDroiyan8Bet{
        /** 玩家id */
        private Long gameUserId;
        /** 标识 ---> 投注记录 */
        private Map<String, UserDroiyan8Info.BetRecord> betRecords;

        public static RdDroiyan8Bet instance(UserDroiyan8Info userDroiyan8Info){
            RdDroiyan8Bet rd = new RdDroiyan8Bet();
            rd.setGameUserId(userDroiyan8Info.getGameUserId());
            rd.setBetRecords(userDroiyan8Info.getBetRecords());
            return rd;
        }
    }

    @Data
    public static class  RdProphetBet{
        /** id场次标识---->投注国家*/
        private Map<String, Integer> betRecords;
        /** 道具是否扣除 */
        private boolean ifNeedTreasure;
        /** 玩家id */
        private Long gameUserId;

        public static RdProphetBet instance(UserProphetInfo userProphet){
            RdProphetBet rd = new RdProphetBet();
            rd.setGameUserId(userProphet.getGameUserId());
            rd.setIfNeedTreasure(userProphet.isIfNeedTreasure());
            rd.setBetRecords(userProphet.getBetRecords());
            return rd;
        }
    }
    @Data
    public static class  RdQuizKingBet{
        /** id场次标识 --->投注记录 */
        private Map<String, List<UserQuizKingInfo.BetRecord>> betRecords;
        /** 玩家id */
        private long gameUserId;

        public static RdQuizKingBet instance(UserQuizKingInfo userQuizKing){
            RdQuizKingBet rd = new RdQuizKingBet();
            rd.setGameUserId(userQuizKing.getGameUserId());
            rd.setBetRecords(userQuizKing.getBetRecords());
            return rd;
        }
    }

}
