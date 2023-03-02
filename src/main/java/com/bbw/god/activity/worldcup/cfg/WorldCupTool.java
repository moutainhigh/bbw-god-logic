package com.bbw.god.activity.worldcup.cfg;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.game.config.Cfg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 世界杯工具类
 * @author: hzf
 * @create: 2022-11-11 15:05
 **/
public class WorldCupTool {

    /**
     * 获取超级16强
     *
     * @return
     */
    public static CfgSuper16 getCfgSuper16(){
        return Cfg.I.getUniqueConfig(CfgSuper16.class);
    }

    /**
     * 根据分组获取竞猜内容
     * @param group
     * @return
     */
    public static  CfgSuper16.CfgQuiz getSuper16Quiz(String group){
        return getCfgSuper16().getQuizs().stream()
                .filter(cfgQuiz -> cfgQuiz.getGroup().equals(group))
                .findFirst().orElse(null);
    }

    /**
     * 获取超级16强
     * @return
     */
    public static List<Integer> getSuper16WinCountrys(){
        List<Integer> winCountrys = new ArrayList<>();
        List<CfgSuper16.CfgQuiz> quizs = getCfgSuper16().getQuizs();
        for (CfgSuper16.CfgQuiz quiz : quizs) {
            winCountrys.addAll(quiz.getWinCountry());
        }
        return winCountrys;
    }
    /**
     * 决战8强
     * @return
     */
    public static List<Integer> getDroiyan8WinCountrys(){
        List<Integer> winCountrys = new ArrayList<>();
        List<CfgDroiyan8.CfgQuiz> quizs = getCfgDroiyan8().getQuizs();
        for (CfgDroiyan8.CfgQuiz quiz : quizs) {
            winCountrys.add(quiz.getWinCountry());
        }
        return winCountrys;
    }

    /**
     * 判断组别是否正常
     * @param group
     * @return
     */
    public static boolean ifGroup(String group){
        List<String> groupList = getCfgSuper16().getQuizs().stream().map(CfgSuper16.CfgQuiz::getGroup).collect(Collectors.toList());
        return groupList.contains(group);
    }



    /**
     * 获取决战8强
     * @return
     */
    public static CfgDroiyan8 getCfgDroiyan8(){
        return Cfg.I.getUniqueConfig(CfgDroiyan8.class);
    }

    /**
     * 根据标识获取
     * @param id
     * @return
     */
    public static CfgDroiyan8.CfgQuiz getCfgDroiyan8Quit(String id){
      return   getCfgDroiyan8().getQuizs().stream()
                .filter(cfgQuiz -> cfgQuiz.getId().equals(id))
                .findFirst().orElse(null);
    }

    /**
     * 获取我是预言家
     * @return
     */
    public static CfgProphet getCfgProphet(){
        return Cfg.I.getUniqueConfig(CfgProphet.class);
    }

    /**
     * 获取我是竞猜王
     * @return
     */
    public static CfgQuizKing getCfgQuizKing(){
        return Cfg.I.getUniqueConfig(CfgQuizKing.class);
    }
    public static CfgQuizKing.CfgBet getCfgQuizKingBetById(String id){
       return getCfgQuizKing().getBets().stream()
                .filter(cfg -> cfg.getId().equals(id))
                .findFirst().orElse(null);
    }

    public static Map<String,List<CfgQuizKing.CfgBet>> getCfgQuizKingBts(){
        List<CfgQuizKing.CfgBet> cfgBetList = getCfgQuizKing().getBets();
        return  cfgBetList.stream().collect(Collectors.groupingBy(CfgQuizKing.CfgBet::getDayDate));
    }

    /**
     * 根据日期获取
     * @param dayDate
     * @return
     */
    public static List<CfgQuizKing.CfgBet> getCfgQuizKingBet(String dayDate){
        List<CfgQuizKing.CfgBet> bets = getCfgQuizKing().getBets();
        return bets.stream().filter(cfgBet -> cfgBet.getDayDate().equals(dayDate)).collect(Collectors.toList());
    }

    /**
     * 获取世界杯国家配置
     * @return
     */
    public static List<CfgCountry> getCfgCountrys(){
        return Cfg.I.get(CfgCountry.class);
    }

    /**
     * 根据国家Id 获取世界杯国家
     * @param countryId
     * @return
     */
    public static CfgCountry getCfgCountry(int countryId){
      return   getCfgCountrys().stream()
                .filter(cfgCountry -> cfgCountry.getCountryId() == countryId)
                .findFirst().orElse(null);
    }

    /**
     * 判断是否还在竞猜时间内
     * @param betBegin 开始竞猜时间
     * @param betEnd  结束竞猜时间
     * @return
     *
     */
    public static boolean ifBet(String betBegin,String betEnd){
        //当前时间
        Date currentTime = new Date();
        Date betBeginDate = DateUtil.fromDateTimeString(betBegin);
        Date betEndDate = DateUtil.fromDateTimeString(betEnd);
        return betBeginDate.before(currentTime) && currentTime.before(betEndDate);
    }

    /**
     * 判断是否还在展示时间内
     * @param showBegin 开始展示时间
     * @param showEnd  结束展示时间
     * @return
     */
    public static boolean ifShow(String showBegin,String showEnd){
        //当前时间
        Date currentTime = new Date();
        Date showBeginDate = DateUtil.fromDateTimeString(showBegin);
        Date showEndDate = DateUtil.fromDateTimeString(showEnd);
        return showBeginDate.before(currentTime) && currentTime.before(showEndDate);
    }

}
