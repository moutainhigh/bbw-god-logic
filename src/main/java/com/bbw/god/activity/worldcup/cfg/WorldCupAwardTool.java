package com.bbw.god.activity.worldcup.cfg;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 世界杯奖励工具
 * @author: hzf
 * @create: 2022-11-12 16:29
 **/
public class WorldCupAwardTool {

    /**
     * 获取玩家超级16猜中次数
     * @param userSuper16Info
     * @return
     */
    public static Integer getSuper16SuccessTime(UserSuper16Info userSuper16Info){
        int num = 0;
        List<CfgSuper16.CfgQuiz> quizs = WorldCupTool.getCfgSuper16().getQuizs();
        for (CfgSuper16.CfgQuiz quiz : quizs) {
            UserSuper16Info.BetRecord betRecord = userSuper16Info.gainBetRecord(quiz.getGroup());
            if (null == betRecord) {
                continue;
            }
            if (null != betRecord && null == betRecord.getBetCountrys()) {
                continue;
            }
            for (Integer betCountry : betRecord.getBetCountrys()) {
                if (quiz.getWinCountry().contains(betCountry)) {
                    num = num + 1;
                }
            }
        }
        return num;
    }


    /**
     * 获取超级16强对应的奖励
     * @param successTime
     * @return
     */
    public static List<Award> getSuper16Award(Integer successTime){
        List<CfgSuper16.CfgQuizAward> quizAwards = WorldCupTool.getCfgSuper16().getQuizAwards();
        CfgSuper16.CfgQuizAward cfgQuizAward = quizAwards.stream().filter(quizAward -> quizAward.getSuccessTimes().equals(successTime)).findFirst().orElse(null);
        if (null == cfgQuizAward){
            return new ArrayList<>();
        }
        return cfgQuizAward.getAwards();
    }

    /**
     * 获取玩家我是预言家获奖次数
     * @param userProphetInfo
     * @return
     */
    public static Integer getProphetSuccessTime(UserProphetInfo userProphetInfo){
        int num = 0;
        List<CfgProphet.CfgQuiz> quizs = WorldCupTool.getCfgProphet().getQuizs();
        for (CfgProphet.CfgQuiz quiz : quizs) {
            Integer betRecord = userProphetInfo.gainBetRecord(quiz.getId());
            if (null == betRecord || 0 == betRecord) {
                continue;
            }
            if (quiz.getWinCountry().equals(betRecord)) {
                num = num + 1;
            }
        }
        return num;
    }

    /**
     * 获取我是预言家奖励
     * @param successTime
     * @return
     */
    public static List<Award> getProphetAward(Integer successTime){
        List<CfgProphet.CfgQuizAward> quizAwards = WorldCupTool.getCfgProphet().getQuizAwards();
        CfgProphet.CfgQuizAward cfgQuizAward = quizAwards.stream().filter(quizAward -> quizAward.getSuccessTimes().equals(successTime)).findFirst().orElse(null);
        if (null == cfgQuizAward) {
            return new ArrayList<>();
        }
        return cfgQuizAward.getAwards();
    }

    /**
     * 获取我是竞猜王奖励数量
     * @param userQuizKingInfo
     * @return
     */
    public static Integer getQuizkingAwrdNum(UserQuizKingInfo userQuizKingInfo, List<CfgQuizKing.CfgBet> bets){
        List<CfgQuizKing.CfgBetAwardRule> betAwardRules = WorldCupTool.getCfgQuizKing().getBetAwardRules();
        CfgQuizKing.CfgBetAwardRule cfgBetFalseRule = betAwardRules.stream().filter(rule -> rule.getSuccess() == false).findFirst().orElse(null);
        CfgQuizKing.CfgBetAwardRule cfgBetTrueRule = betAwardRules.stream().filter(rule -> rule.getSuccess()).findFirst().orElse(null);
        int transport = cfgBetFalseRule.getNum();
        int win = cfgBetTrueRule.getNum();
        int num = 0;
        for (CfgQuizKing.CfgBet bet : bets) {
            List<UserQuizKingInfo.BetRecord> betRecords = userQuizKingInfo.gainBetRecords(bet.getId());
            if (ListUtil.isEmpty(betRecords)){
                continue;
            }
            for (UserQuizKingInfo.BetRecord betRecord : betRecords) {
                if (betRecord.getBetCountry().equals(bet.getWinCountry())){
                    num = num + win * betRecord.getBetNum();
                } else {
                    num = num + transport * betRecord.getBetNum();
                }
            }
        }
        return num;
    }

    /**
     * 只计算赢的奖励数量
     * @param userQuizKingInfo
     * @param bets
     * @return
     */
    public static Integer getQuizkingWinAwrdNum(UserQuizKingInfo userQuizKingInfo, List<CfgQuizKing.CfgBet> bets){
        List<CfgQuizKing.CfgBetAwardRule> betAwardRules = WorldCupTool.getCfgQuizKing().getBetAwardRules();
        CfgQuizKing.CfgBetAwardRule cfgBetFalseRule = betAwardRules.stream().filter(rule -> rule.getSuccess() == false).findFirst().orElse(null);
        CfgQuizKing.CfgBetAwardRule cfgBetTrueRule = betAwardRules.stream().filter(rule -> rule.getSuccess()).findFirst().orElse(null);
        int transport = cfgBetFalseRule.getNum();
        int win = cfgBetTrueRule.getNum();
        int num = 0;
        for (CfgQuizKing.CfgBet bet : bets) {
            List<UserQuizKingInfo.BetRecord> betRecords = userQuizKingInfo.gainBetRecords(bet.getId());
            if (ListUtil.isEmpty(betRecords)){
                continue;
            }
            for (UserQuizKingInfo.BetRecord betRecord : betRecords) {
                if (betRecord.getBetCountry().equals(bet.getWinCountry())){
                    num = num + win * betRecord.getBetNum();
                }
            }
        }
        return num;
    }

    /**
     * 获取我是竞猜王的奖励
     * @param num
     * @return
     */
    public static List<Award> getQuizkingAwrd(int num){
        List<Award> awards = new ArrayList<>();
        Integer betAwardId = WorldCupTool.getCfgQuizKing().getBetAwardId();
        Award award = new Award(betAwardId, AwardEnum.FB, num);
        awards.add(award);
        return awards;
    }

    /**
     * 决战8强连中集合
     * @param userDroiyan8Info
     * @return
     */
    public static List<Integer> getDroiyan8List(UserDroiyan8Info userDroiyan8Info){
        List<Integer> winList = new ArrayList<>();
        List<CfgDroiyan8.CfgQuiz> quizs = WorldCupTool.getCfgDroiyan8().getQuizs();
        for (CfgDroiyan8.CfgQuiz quiz : quizs) {
            Integer betCountry = userDroiyan8Info.gainbetCountry(quiz.getId());
            if (null == betCountry || 0 == betCountry) {
                winList.add(-1);
                continue;
            }
            if (quiz.getWinCountry().equals(betCountry)) {
                winList.add(1);
            }else {
                winList.add(0);
            }
        }
        return winList;
    }

    public static int countDroiyan8WinNum(List<Integer> winList){
        int num = 0;
        for (Integer integer : winList) {
            if (1 == integer) {
                num = num + 1;
            }
        }
        return num;
    }

    /**
     * 决战8强连中的奖励数量
     * @param winList
     * @return
     */
    public static Integer getDroiyan8AwardNum(List<Integer> winList){
        Map<Integer,Integer> winMap = new HashMap<>();
        List<CfgDroiyan8.CfgContinuousSuccess> continuousSuccess = WorldCupTool.getCfgDroiyan8().getContinuousSuccess();
        for (CfgDroiyan8.CfgContinuousSuccess continuousSuccessTimes : continuousSuccess) {
            winMap.put(continuousSuccessTimes.getContinuousSuccessTimes(),continuousSuccessTimes.getNum());
        }
        int num = 0;
        int i = 0;
        for (Integer win : winList) {
            if (1 == win) {
                num = num + 200;
                i++;
            } else {
                i = 0;
                if (0 == win) {
                    num = num + 50;
                }
            }
            if (i >= 2) {
                num = num + winMap.get(i) - winMap.get(i-1);
            }
        }
        return num;
    }

    /**
     * 决战8强奖励
     * @param num
     * @return
     */
    public static List<Award> getDroiyan8Award(int num){
        List<Award> awards = new ArrayList<>();
        Integer quizAwardId = WorldCupTool.getCfgDroiyan8().getBetAwardId();
        Award award = new Award(quizAwardId, AwardEnum.FB, num);
        awards.add(award);
        return awards;

    }

}
