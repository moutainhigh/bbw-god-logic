package com.bbw.god.activity.worldcup.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.worldcup.cfg.CfgDroiyan8;
import com.bbw.god.activity.worldcup.cfg.WorldCupTool;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 决战8强返回类
 * @author: hzf
 * @create: 2022-11-12 09:34
 **/
@Data
public class RdDroiyan8 extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 参赛国家 : 分组情况 **/
    private List<RdDivideGroup> Droiyan8List;
    private String dateInfo;
    private List<Integer> Droiyan8WinCountrys;
    /** 是否已经开奖 */
    private Integer ifOpenPrizeTime;

    public static RdDroiyan8 instance(String dateInfo, CfgDroiyan8 cfgDroiyan8, UserDroiyan8Info userDroiyan8Info){
        RdDroiyan8 rdDroiyan8 = new RdDroiyan8();

        List<RdDivideGroup> rdDivideGroups = new ArrayList<>();
        boolean ifShow = WorldCupTool.ifShow(cfgDroiyan8.getShowBegin(), cfgDroiyan8.getShowEnd());
        // 是否到展示时间
        rdDroiyan8.setIfOpenPrizeTime(ifShow?1:0);

        for (CfgDroiyan8.CfgQuiz quiz : cfgDroiyan8.getQuizs()) {
            RdDivideGroup rdDivideGroup = new RdDivideGroup();
            rdDivideGroup.setId(quiz.getId());
//            rdDivideGroup.setCompeteCountries(quiz.getCompeteCountries());
            //是否开始
            boolean ifBet = WorldCupTool.ifBet(quiz.getBetBegin(), quiz.getBetEnd());
            rdDivideGroup.setStatus(ifBet ? 1 : 0);
            //玩家的投注记录
            Integer country =  null == userDroiyan8Info ? 0 : userDroiyan8Info.gainbetCountry(quiz.getId());
            rdDivideGroup.setBetCountry(country);

            if (ifBet) {
                //判断是否有投注
                int betStatus = 0 == country ? 1 : 2;
                rdDivideGroup.setStatus(betStatus);
            }
            //判断活动是否已经结束
            Date betEedDate = DateUtil.fromDateTimeString(quiz.getBetEnd());
            boolean betEed = betEedDate.before(new Date());
            if (betEed) {
                rdDivideGroup.setStatus(3);
            }

            rdDivideGroup.setWinCountry(quiz.getWinCountry());
            rdDivideGroups.add(rdDivideGroup);
        }
        rdDroiyan8.setDateInfo(dateInfo);
        rdDroiyan8.setDroiyan8List(rdDivideGroups);
        return rdDroiyan8;
    }


    @Data
    public static class RdDivideGroup{
        /** 场次 标识 */
        private String id;
        /** 0:未开始，1:可投注 2:已经投注 3:已经结束 */
        private int status;
//        private List<Integer> competeCountries;
        /** 玩家投注记录 */
        private Integer betCountry;
        /** 晋级国家 */
        private Integer winCountry;
    }
}
